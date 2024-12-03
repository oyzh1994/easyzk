package cn.oyzh.easyzk.trees.node;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.TreeChildFilterEvent;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.treeView.RichTreeCell;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import cn.oyzh.fx.plus.node.NodeLifeCycle;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * zk树
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class ZKNodeTreeView extends RichTreeView implements NodeLifeCycle {

    @Getter
    @Setter
    @Accessors(fluent = true, chain = false)
    private ZKClient client;

    public ZKConnect connect() {
        return this.client.connect();
    }

    @Override
    protected void initTreeView() {
        super.initTreeView();
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
    }

    @Override
    public ZKNodeTreeItemFilter itemFilter() {
        try {
            // 初始化过滤器
            if (this.itemFilter == null) {
                ZKNodeTreeItemFilter filter = new ZKNodeTreeItemFilter();
                filter.initFilters();
                this.itemFilter = filter;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ZKNodeTreeItemFilter) this.itemFilter;
    }

    @Override
    public ZKNodeTreeItem getRoot() {
        return (ZKNodeTreeItem) super.getRoot();
    }

    @Override
    public ZKNodeTreeItem getSelectedItem() {
        return (ZKNodeTreeItem) super.getSelectedItem();
    }

    /**
     * 寻找zk节点
     *
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull String targetPath) {
        return this.findNodeItem(this.getRoot(), targetPath);
    }

    /**
     * 寻找zk节点
     *
     * @param root       根节点
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull ZKNodeTreeItem root, @NonNull String targetPath) {
        // 节点对应，返回数据
        if (targetPath.equals(root.nodePath())) {
            return root;
        }
        // 互相不包含，返回null
        if (!targetPath.contains(root.nodePath()) && !root.nodePath().startsWith(targetPath)) {
            return null;
        }
        // 子节点为空，返回null
        if (root.isChildEmpty()) {
            return null;
        }
        // 遍历子节点，寻找匹配节点
        for (ZKNodeTreeItem item : root.itemChildren()) {
            ZKNodeTreeItem treeItem = this.findNodeItem(item, targetPath);
            // 返回节点信息
            if (treeItem != null) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 树节点过滤
     */
    @EventSubscribe
    private void treeChildFilter(TreeChildFilterEvent event) {
        this.itemFilter().initFilters();
        this.filter();
    }

    @Override
    public void expand() {
        ZKNodeTreeItem item = this.getSelectedItem();
        if (item != null) {
            item.expandAll();
            this.select(item);
        }
    }

    @Override
    public void collapse() {
        ZKNodeTreeItem item = this.getSelectedItem();
        if (item != null) {
            item.collapseAll();
            this.select(item);
        }
    }

    /**
     * 节点添加
     *
     * @param nodePath 节点路径
     */
    public void onNodeAdded(String nodePath) {
        try {
            String pPath = ZKNodeUtil.getParentPath(nodePath);
            // 寻找节点
            ZKNodeTreeItem parent = this.findNodeItem(pPath);
            // 父节点不存在
            if (parent == null) {
                JulLog.warn("{}: 未找到节点的父节点，无法处理节点！", nodePath);
                return;
            }
            // 获取节点
            ZKNodeTreeItem item = parent.getNodeItem(pPath);
            // 刷新节点
            if (item != null) {
                item.refreshNode();
                JulLog.info("节点已存在, 更新节点.");
            } else if (parent.isLoaded()) {// 添加节点
                parent.refreshStat();
                parent.addChild(nodePath);
                JulLog.info("节点不存在, 添加节点.");
            } else {// 加载子节点
                parent.refreshStat();
                parent.loadChild(false);
                JulLog.info("父节点未加载, 加载父节点.");
            }
            // 过滤节点
            parent.doFilter(this.itemFilter());
            // 选中此节点
            if (item == null) {
                item = parent.getNodeItem(nodePath);
            }
            if (item != null) {
                ZKNodeTreeItem finalItem = item;
                FXUtil.runPulse(() -> this.selectAndScroll(finalItem));
            }
        } catch (Exception ex) {
            JulLog.warn("新增节点事件处理失败！", ex);
        }
    }

    /**
     * 节点已添加
     *
     * @param nodePath 节点路径
     */
    public void onNodeCreated(String nodePath) {
        if (this.client.isLastCreate(nodePath)) {
            this.client.clearLastCreate();
            return;
        }
        try {
            String pPath = ZKNodeUtil.getParentPath(nodePath);
            // 寻找节点
            ZKNodeTreeItem parent = this.findNodeItem(pPath);
            // 父节点不存在
            if (parent == null) {
                JulLog.warn("{}: 未找到节点的父节点，无法处理节点！", nodePath);
                return;
            }
            // 获取节点
            ZKNodeTreeItem item = parent.getNodeItem(nodePath);
            // 刷新节点
            if (item != null) {
                item.refreshNode();
                JulLog.info("节点已存在, 更新节点.");
            } else if (parent.isLoaded()) {// 更新父节点状态，并标记状态
                parent.refreshStat();
                parent.setBeChildChanged();
                JulLog.info("节点不存在, 父节点已加载, 标记父节点状态.");
            } else {// 更新父节点状态
                parent.refreshStat();
                JulLog.info("节点不存在, 父节点未加载, 更新父节点状态.");
            }
        } catch (Exception ex) {
            JulLog.warn("节点已新增事件处理失败！", ex);
        }
    }

    /**
     * 节点已删除
     *
     * @param nodePath 节点路径
     */
    public void onNodeRemoved(String nodePath) {
        if (this.client.isLastDelete(nodePath)) {
            this.client.clearLastDelete();
            return;
        }
        try {
            String pPath = ZKNodeUtil.getParentPath(nodePath);
            // 寻找节点
            ZKNodeTreeItem parent = this.findNodeItem(pPath);
            // 父节点不存在
            if (parent == null) {
                JulLog.warn("{}: 未找到节点的父节点，无法处理节点！", nodePath);
                return;
            }
            // 寻找节点
            ZKNodeTreeItem item = parent.getNodeItem(nodePath);
            // 更新信息
            if (item != null) {
                item.setBeDeleted();
                JulLog.info("节点存在, 标记节点状态为删除.");
            } else {// 更新父节点状态
                parent.refreshStat();
                JulLog.info("节点不存在, 父节点未加载, 更新父节点状态.");
            }
        } catch (Exception ex) {
            JulLog.warn("节点已删除事件处理失败！", ex);
        }
    }

    /**
     * 节点已变更
     *
     * @param nodePath 节点路径
     */
    public void onNodeChanged(String nodePath) {
        if (this.client.isLastUpdate(nodePath)) {
            this.client.clearLastUpdate();
            return;
        }
        try {
            // 寻找节点
            ZKNodeTreeItem item = this.findNodeItem(nodePath);
            // 更新信息
            if (item != null) {
                item.setBeChanged();
            } else {
                JulLog.warn("{}: 未找到被修改节点，无法处理节点！", nodePath);
            }
        } catch (Exception ex) {
            JulLog.warn("节点已修改事件处理失败！", ex);
        }
    }

    /**
     * 获取全部zk子节点列表
     *
     * @return List<ZKNodeTreeItem>
     */
    private List<ZKNodeTreeItem> getAllNodeItem() {
        List<ZKNodeTreeItem> list = new ArrayList<>();
        this.getAllNodeItem(this.getRoot(), list);
        return list;
    }

    /**
     * 获取全部zk子节点列表
     *
     * @param item zk节点
     * @param list zk子节点列表
     */
    private void getAllNodeItem(ZKNodeTreeItem item, List<ZKNodeTreeItem> list) {
        if (item != null) {
            list.add(item);
            for (TreeItem<?> treeItem : item.unfilteredChildren()) {
                if (treeItem instanceof ZKNodeTreeItem nodeTreeItem) {
                    this.getAllNodeItem(nodeTreeItem, list);
                }
            }
        }
    }

    /**
     * 认证变更事件
     *
     * @param auth 认证信息
     */
    public void authChanged(ZKAuth auth) throws Exception {
        this.client.addAuth(auth.getUser(), auth.getPassword());
        for (ZKNodeTreeItem item : this.getAllNodeItem()) {
            if (item.isNeedAuth() || ZKACLUtil.existDigest(item.acl(), auth.getUser())) {
                item.authChanged();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * 加载根节点
     */
    public void loadRoot() {
        // 禁用树
        this.disable();
        try {
            // 获取根节点
            ZKNode rootNode = ZKNodeUtil.getNode(this.client, "/");
            // 生成根节点
            ZKNodeTreeItem rootItem = new ZKNodeTreeItem(rootNode, this);
            // 设置根节点
            this.setRoot(rootItem);
            // 加载根节点
            rootItem.loadRoot();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        } finally {
            // 启用树
            this.enable();
        }
    }

    /**
     * 是否有未保存数据的节点
     *
     * @return 结果
     */
    public boolean hasUnsavedData() {
        List<ZKNodeTreeItem> items = this.getAllNodeItem();
        for (ZKNodeTreeItem item : items) {
            if (item.isDataUnsaved()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNodeDestroy() {
//        if (this.client != null) {
//            this.client.setCacheFilter(null);
//        }
    }

    @Override
    public void onNodeInitialize() {

    }
}
