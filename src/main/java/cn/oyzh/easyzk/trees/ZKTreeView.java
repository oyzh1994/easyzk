package cn.oyzh.easyzk.trees;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.msg.ZKAuthMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeAddMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeAddedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeDeletedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdatedMsg;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.root.ZKRootTreeItem;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventGroup;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.trees.RichTreeView;
import cn.oyzh.fx.plus.util.MouseUtil;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * zk树
 *
 * @author oyzh
 * @since 2023/1/29
 */
//@Slf4j
@Accessors(chain = true, fluent = true)
public class ZKTreeView extends RichTreeView {

    /**
     * 搜索中标志位
     */
    @Getter
    private volatile boolean searching;

    /**
     * 配置储存对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    public ZKTreeView() {
        this.dragContent = "zk_tree_drag";
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new ZKTreeCell());
        // 初始化根节点
        super.root(new ZKRootTreeItem(this));
        this.root().extend();
    }

    @Override
    protected void initEvenListener() {
        super.initEvenListener();
        // 暂停按键处理
        KeyListener.listenReleased(this, KeyCode.PAUSE, event -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof ZKConnectTreeItem treeItem) {
                treeItem.closeConnect();
            } else if (item instanceof ZKNodeTreeItem nodeTreeItem) {
                nodeTreeItem.root().closeConnect();
            }
        });
    }

    @Override
    public ZKTreeItemFilter itemFilter() {
        // 初始化过滤器
        if (this.itemFilter == null) {
            ZKTreeItemFilter filter = SpringUtil.getBean(ZKTreeItemFilter.class);
            filter.initFilters();
            this.itemFilter = filter;
        }
        return (ZKTreeItemFilter) this.itemFilter;
    }

    @Override
    public ZKRootTreeItem root() {
        return (ZKRootTreeItem) this.getRoot();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (ZKConnectTreeItem treeItem : this.root().getConnectedItems()) {
            ThreadUtil.startVirtual(treeItem::closeConnect);
        }
    }

    /**
     * 寻找zk节点
     *
     * @param targetPath 目标路径
     * @param zkInfo     zk信息
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull String targetPath, @NonNull ZKInfo zkInfo) {
        List<ZKConnectTreeItem> connectTreeItems = this.root().getConnectedItems();
        Optional<ZKConnectTreeItem> itemOptional = connectTreeItems.parallelStream().filter(c -> c.value() == zkInfo).findFirst();
        return itemOptional.map(item -> item.findNodeItem(targetPath)).orElse(null);
    }

    /**
     * 获取所有zk节点
     *
     * @return zk节点集合
     */
    public List<ZKNodeTreeItem> getAllNodeItem() {
        List<ZKNodeTreeItem> list = new ArrayList<>();
        List<ZKConnectTreeItem> connectTreeItems = this.root().getConnectedItems();
        for (ZKConnectTreeItem connectTreeItem : connectTreeItems) {
            list.addAll(connectTreeItem.getAllNodeItem());
        }
        return list;
    }

    /**
     * zk节点事件
     *
     * @param event 事件
     */
    @EventGroup(value = ZKEventGroups.NODE_MSG, async = true, verbose = true)
    private void zkNodeMsg(Event<EventMsg> event) {
        EventMsg msg = event.data();
        switch (msg.name()) {
            case ZKEventTypes.ZK_NODE_ADDED -> nodeAdded((ZKNodeAddedMsg) msg);
            case ZKEventTypes.ZK_NODE_DELETED -> nodeDeleted((ZKNodeDeletedMsg) msg);
            case ZKEventTypes.ZK_NODE_UPDATED -> nodeUpdated((ZKNodeUpdatedMsg) msg);
        }
    }

    /**
     * zk节点新增事件，消息监听
     *
     * @param msg 消息
     */
    private void nodeAdded(ZKNodeAddedMsg msg) {
        if (msg.client().isLastCreate(msg.path())) {
            msg.client().clearLastCreate();
        } else {
            this.nodeAdd(msg.path(), msg.info());
        }
    }

    /**
     * zk节点新增事件，手动操作
     *
     * @param msg 消息
     */
    @EventReceiver(value = ZKEventTypes.ZK_NODE_ADD, async = true, verbose = true)
    private void nodeAdd(ZKNodeAddMsg msg) {
        this.nodeAdd(msg.path(), msg.info());
    }

    /**
     * zk节点新增
     *
     * @param path 路径
     * @param info zk信息
     */
    private void nodeAdd(String path, ZKInfo info) {
        try {
            // 父节点路径
            String pPath = ZKNodeUtil.getParentPath(path);
            // 寻找节点
            ZKNodeTreeItem parent = this.findNodeItem(pPath, info);
            // 父节点不存在
            if (parent == null) {
                StaticLog.warn("{}: 未找到新增节点的父节点，无法处理节点！", path);
                return;
            }
            // 获取节点
            ZKNodeTreeItem child = parent.getChild(path);
            // 刷新节点
            if (child != null) {
                child.refreshNode();
                StaticLog.info("节点已存在, 更新树节点.");
            } else if (parent.loaded()) {// 添加节点
                parent.refreshStat();
                parent.addChild(path);
                StaticLog.info("节点不存在, 添加树节点.");
            } else if (parent.client().isLastCreate(path)) {// 加载子节点
                parent.refreshStat();
                parent.loadChildQuiet();
                StaticLog.info("父节点未加载, 加载父节点.");
            }
            // 过滤节点
            parent.doFilter(this.itemFilter);
        } catch (Exception ex) {
            StaticLog.warn("新增节点失败！", ex);
        }
    }

    /**
     * zk节点修改事件
     *
     * @param msg 消息
     */
    private void nodeUpdated(ZKNodeUpdatedMsg msg) {
        if (msg.client().isLastUpdate(msg.path())) {
            msg.client().clearLastUpdate();
            return;
        }
        try {
            // 寻找节点
            ZKNodeTreeItem item = this.findNodeItem(msg.path(), msg.info());
            // 更新信息
            if (item != null) {
                item.setBeUpdated(msg.data());
            } else {
                StaticLog.warn("{}: 未找到被修改节点，无法处理节点！", msg.decodeNodePath());
            }
        } catch (Exception ex) {
            StaticLog.warn("修改节点失败！", ex);
        }
    }

    /**
     * zk节点删除事件
     *
     * @param msg 消息
     */
    private void nodeDeleted(ZKNodeDeletedMsg msg) {
        if (msg.client().isLastDelete(msg.path())) {
            msg.client().clearLastDelete();
            return;
        }
        try {
            // 寻找节点
            ZKNodeTreeItem item = this.findNodeItem(msg.path(), msg.info());
            // 更新信息
            if (item != null) {
                item.setBeDeleted();
            } else {
                StaticLog.warn("{}: 未找到被删除节点，无法处理节点！", msg.decodeNodePath());
            }
        } catch (Exception ex) {
            StaticLog.warn("删除节点失败！", ex);
        }
    }

    /**
     * 认证成功事件
     *
     * @param authMsg 认证消息
     */
    @EventReceiver(value = ZKEventTypes.ZK_AUTH_SUCCESS, async = true, verbose = true)
    private void authSuccess(ZKAuthMsg authMsg) {
        ZKNodeTreeItem authedItem = authMsg == null ? null : authMsg.item();
        boolean activity = authedItem == this.getSelectedItem();
        // 对所有需要认证执行节点刷新
        for (ZKNodeTreeItem item : this.getAllNodeItem()) {
            if (item.needAuth()) {
                item.refreshNode();
            }
        }
        // 对待认证节点执行图标刷新
        if (authedItem != null) {
            authedItem.flushGraphic();
        }
        // 如果是当前激活节点，则重新选中
        if (activity) {
            this.select(null);
            this.select(authedItem);
        }
    }

    /**
     * 认证加入事件
     *
     * @param auth 认证信息
     */
    @EventReceiver(value = ZKEventTypes.ZK_AUTH_ADDED, async = true, verbose = true)
    @EventReceiver(value = ZKEventTypes.ZK_AUTH_ENABLE, async = true, verbose = true)
    private void authJoined(ZKAuth auth) {
        if (this.setting.isAutoAuth()) {
            try {
                // 获取已连接的zk连接
                List<ZKConnectTreeItem> connectTreeItems = this.root().getConnectedItems();
                // 遍历添加认证信息，并手动触发认证事件
                for (ZKConnectTreeItem item : connectTreeItems) {
                    item.client().addAuth(auth.getUser(), auth.getPassword());
                }
                // 执行认证成功业务
                this.authSuccess(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索开始事件
     */
    @EventReceiver(value = ZKEventTypes.ZK_SEARCH_START, async = true, verbose = true)
    private void onSearchStart() {
        this.searching = true;
        this.filter();
    }

    /**
     * 搜索结束事件
     */
    @EventReceiver(value = ZKEventTypes.ZK_SEARCH_FINISH, async = true, verbose = true)
    private void onSearchFinish() {
        this.searching = false;
        this.filter();
    }

    /**
     * 树节点过滤
     */
    @EventReceiver(value = ZKEventTypes.TREE_CHILD_FILTER, async = true, verbose = true)
    private void onTreeChildFilter() {
        this.itemFilter().initFilters();
        this.filter();
    }

    /**
     * 重写父类方法，用于展开当前选中的项
     */
    @Override
    public void expand() {
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof ZKNodeTreeItem treeItem) {
            treeItem.expandAll(); // 展开所有子项
        } else if (item instanceof ZKConnectTreeItem treeItem) {
            treeItem.extend(); // 扩展连接项
            if (!treeItem.isChildEmpty()) {
                treeItem.firstChild().expandAll(); // 展开第一个子项的所有子项
            }
        } else if (item instanceof ZKTreeItem<?> treeItem) {
            treeItem.extend(); // 扩展普通项
        }
        if (item != null) {
            this.select(item); // 选中展开的项
        }
    }

    /**
     * 重写父类方法，用于折叠当前选中的树项
     */
    @Override
    public void collapse() {
        // 获取当前选中的树项
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof ZKNodeTreeItem treeItem) {
            // 如果当前树项是ZKNodeTreeItem类型，则折叠所有子树项
            treeItem.collapseAll();
        } else if (item instanceof ZKConnectTreeItem treeItem) {
            // 如果当前树项是ZKConnectTreeItem类型，则折叠当前树项，并折叠第一个子树项
            treeItem.collapse();
            if (!treeItem.isChildEmpty()) {
                treeItem.firstChild().collapseAll();
            }
        } else if (item instanceof ZKTreeItem<?> treeItem) {
            // 如果当前树项是ZKTreeItem类型，则只折叠当前树项
            treeItem.collapse();
        }
        // 选中折叠后的树项
        if (item != null) {
            this.select(item);
        }
    }
}
