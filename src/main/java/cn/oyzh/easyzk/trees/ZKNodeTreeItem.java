package cn.oyzh.easyzk.trees;

import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.controller.auth.ZKAuthController;
import cn.oyzh.easyzk.controller.node.ZKNodeAddController;
import cn.oyzh.easyzk.controller.node.ZKNodeExportController;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.thread.BackgroundService;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/1/30
 */
//@Slf4j
public class ZKNodeTreeItem extends ZKTreeItem<ZKNodeTreeItemValue> {

    /**
     * zk节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private final ZKNode value;

    /**
     * 字符集
     */
    @Getter
    @Setter
    private Charset charset = Charset.defaultCharset();

    /**
     * 是否被其他连接删除
     */
    @Getter
    private boolean beDeleted;

    /**
     * 被其他连接修改的数据
     */
    @Getter
    private byte[] updateData;

    /**
     * 忽略删除数据
     */
    @Getter
    @Setter
    private boolean ignoreDeleted;

    /**
     * 忽略修改数据
     */
    @Getter
    @Setter
    private boolean ignoreUpdated;

    /**
     * 已加载标志位
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private volatile boolean loaded;

    /**
     * 加载中标志位
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private volatile boolean loading;

    /**
     * 取消标志位
     */
    private volatile boolean canceled;

    /**
     * 连接节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private final ZKConnectTreeItem root;

    /**
     * 设置被删除状态
     */
    public void setBeDeleted() {
        this.beDeleted = true;
        this.ignoreDeleted = false;
        this.flushValue();
    }

    /**
     * 是否被修改状态
     *
     * @return 结果
     */
    public boolean isBeUpdated() {
        return this.updateData != null;
    }

    /**
     * 设置更新状态
     *
     * @param updateData 更新数据
     */
    public void setBeUpdated(byte[] updateData) {
        this.updateData = updateData;
        this.ignoreUpdated = false;
        this.flushValue();
    }

    /**
     * 清除数据状态
     */
    public void clearStatus() {
        this.beDeleted = false;
        this.updateData = null;
        this.flushValue();
    }

    /**
     * 获取节点状态属性
     *
     * @return 节点状态属性
     */
    public ObjectProperty<Stat> statProperty() {
        return this.value.statProperty();
    }

    /**
     * 获取节点配额属性
     *
     * @return 配额属性
     */
    public ObjectProperty<StatsTrack> quotaProperty() {
        return this.value.quotaProperty();
    }

    /**
     * 获取节点权限属性
     *
     * @return 节点权限属性
     */
    public ObjectProperty<List<ZKACL>> aclProperty() {
        return this.value.aclProperty();
    }

    /**
     * 数据属性
     */
    private SimpleObjectProperty<byte[]> dataProperty;

    /**
     * 获取数据属性
     *
     * @return 数据属性
     */
    public SimpleObjectProperty<byte[]> dataProperty() {
        if (this.dataProperty == null) {
            this.dataProperty = new SimpleObjectProperty<>();
        }
        return this.dataProperty;
    }

    /**
     * 数据监听器
     */
    private ChangeListener<byte[]> dataListener;

    /**
     * 状态监听器
     */
    private ChangeListener<Stat> statListener;

    /**
     * 权限监听器
     */
    private ChangeListener<List<ZKACL>> aclListener;

    /**
     * 配额监听器
     */
    private ChangeListener<StatsTrack> quotaListener;

    /**
     * 添加数据监听器
     *
     * @param dataListener 数据监听器
     */
    public void addDataListener(ChangeListener<byte[]> dataListener) {
        if (this.dataListener != null) {
            this.dataProperty().removeListener(this.dataListener);
        }
        if (dataListener != null) {
            this.dataProperty().addListener(dataListener);
        }
        this.dataListener = dataListener;
    }

    /**
     * 添加状态监听器
     *
     * @param statListener 数据监听器
     */
    public void addStatListener(ChangeListener<Stat> statListener) {
        if (this.statListener != null) {
            this.statProperty().removeListener(this.statListener);
        }
        if (statListener != null) {
            this.statProperty().addListener(statListener);
        }
        this.statListener = statListener;
    }

    /**
     * 添加权限监听器
     *
     * @param aclListener 权限监听器
     */
    public void addAclListener(ChangeListener<List<ZKACL>> aclListener) {
        if (this.aclListener != null) {
            this.aclProperty().removeListener(this.aclListener);
        }
        if (aclListener != null) {
            this.aclProperty().addListener(aclListener);
        }
        this.aclListener = aclListener;
    }

    /**
     * 添加配额监听器
     *
     * @param quotaListener 配额监听器
     */
    public void addQuotaListener(ChangeListener<StatsTrack> quotaListener) {
        if (this.quotaListener != null) {
            this.quotaProperty().removeListener(this.quotaListener);
        }
        if (quotaListener != null) {
            this.quotaProperty().addListener(quotaListener);
        }
        this.quotaListener = quotaListener;
    }

    /**
     * 清除监听器
     */
    public void clearListener() {
        this.addDataListener(null);
        this.addStatListener(null);
        this.addAclListener(null);
        this.addQuotaListener(null);
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    public byte[] data() {
        if (this.dataUnsaved()) {
            return this.dataProperty.get();
        }
        try {
            if (!this.value.nodeDataLoaded()) {
                ZKNodeUtil.refreshData(this.client(), this.value);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.value.nodeData();
    }

    /**
     * 获取数据字符串
     *
     * @return 数据字符串
     */
    public String dataStr() {
        byte[] bytes = this.data();
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return new String(this.data(), this.charset);
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void data(byte[] data) {
        this.dataProperty().set(data);
        this.flushValue();
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void data(String data) {
        if (!Objects.equals(this.dataStr(), data)) {
            this.dataProperty().set(data.getBytes(this.charset));
            this.flushValue();
        }
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if (this.dataProperty != null) {
            this.dataProperty.set(null);
            this.flushValue();
        }
    }

    /**
     * 数据是否未保存
     *
     * @return 结果
     */
    public boolean dataUnsaved() {
        if (this.dataProperty == null) {
            return false;
        }
        return this.dataProperty.get() != null;
    }

    public ZKNodeTreeItem(@NonNull ZKNode value, @NonNull ZKConnectTreeItem root) {
        super(root.getTreeView());
        this.root = root;
        this.value = value;
        this.setFilterable(true);
        this.setValue(new ZKNodeTreeItemValue(this));
        this.initValue();
        this.flushValue();
        if (this.value.rootNode()) {
            super.addEventHandler(treeNotificationEvent(), this.treeEventEventHandler());
        } else {
            this.visibleProperty().addListener((observableValue, aBoolean, t1) -> super.addEventHandler(treeNotificationEvent(), this.treeEventEventHandler()));
        }
    }

    /**
     * 事件处理
     */
    private EventHandler<TreeModificationEvent<ZKNodeTreeItem>> treeEventEventHandler = null;

    private EventHandler<TreeModificationEvent<ZKNodeTreeItem>> treeEventEventHandler() {
        if (this.treeEventEventHandler == null) {
            this.treeEventEventHandler = event -> {
                if (Objects.equals(this, event.getTreeItem())) {
                    if (event.getEventType() == branchCollapsedEvent()) {
                        this.clearChildValue();
                    } else if (event.getEventType() == branchExpandedEvent()) {
                        this.initChildValue();
                    } else if (event.getEventType() == childrenModificationEvent()) {
                        try {
                            // 添加、移除则刷新状态
                            if (event.wasAdded() || event.wasRemoved()) {
                                this.refreshStat();
                            }
                            ZKEventUtil.treeChildChanged();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            };
        }
        return this.treeEventEventHandler;
    }

    /**
     * 初始化值
     */
    protected void initValue() {
        try {
            if (this.getValue().getChildren().isEmpty()) {
                this.getValue().flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 销毁值
     */
    protected void clearValue() {
        try {
            if (!this.getValue().getChildren().isEmpty()) {
                this.getValue().clearChild();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 初始化子节点值
     */
    protected void initChildValue() {
        try {
            for (ZKNodeTreeItem showChild : this.showChildren()) {
                showChild.initValue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 销毁子节点值
     */
    protected void clearChildValue() {
        try {
            for (ZKNodeTreeItem showChild : this.showChildren()) {
                showChild.clearValue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 刷新值
     */
    private void flushValue() {
        BackgroundService.submit(() -> this.getValue().flush());
    }

    @Override
    public synchronized void doFilter(RichTreeItemFilter itemFilter) {
        super.doFilter(itemFilter);
        this.flushValue();
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.root.client();
    }

    /**
     * 获取节点路径
     *
     * @return 节点路径
     */
    public String nodePath() {
        return this.value.nodePath();
    }

    /**
     * 加载子节点
     */
    public void loadChild() {
        if (this.canLoadSub()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> this.loadChildes(false))
                    .onSuccess(this::extend)
                    .onFinish(() -> {
                        this.stopWaiting();
                        this.flushValue();
                    })
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        }
    }

    /**
     * 加载子节点，静默模式
     */
    public void loadChildQuiet() {
        if (this.canLoadSub()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> this.loadChildes(false))
                    .onSuccess(this::extend)
                    .onError(Throwable::printStackTrace)
                    .build();
            ThreadUtil.startVirtual(task);
        }
    }

    @Override
    public void free() {
        if (!this.loaded()) {
            this.loadChild();
        } else {
            super.free();
        }
    }

    /**
     * 获取图标地址
     *
     * @return 图标地址
     */
    public String getSVGUrl() {
        return this.getValue().getSVGUrl();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.loading) {
            MenuItem cancel = MenuItemExt.newItem("取消操作", new SVGGlyph("/font/close.svg", "11"), "取消操作", this::cancel);
            items.add(cancel);
        } else {
            MenuItem auth = MenuItemExt.newItem("认证节点", new SVGGlyph("/font/unlock.svg", "12"), "对zk节点进行认证", this::authNode);
            if (!this.ephemeral()) {
                MenuItem add = MenuItemExt.newItem("添加节点", new SVGGlyph("/font/add.svg", "12"), "添加zk子节点", this::addNode);
                items.add(add);
            }
            if (!this.value.rootNode() && this.value.subNode() && !this.ephemeral()) {
                MenuItem rename = MenuItemExt.newItem("节点更名", new SVGGlyph("/font/edit-square.svg", "12"), "更改节点名称(快捷键f2)", this::rename);
                items.add(rename);
            }
            if (!this.value.rootNode()) {
                MenuItem delete = MenuItemExt.newItem("删除节点", new SVGGlyph("/font/delete.svg", "12"), "删除此zk节点及子节点(快捷键delete)", this::delete);
                items.add(delete);
            }
            if (this.value.parentNode()) {
                MenuItem reload = MenuItemExt.newItem("重新载入", new SVGGlyph("/font/reload.svg", "12"), "重新加载此zk节点子节点", this::reloadChild);
                MenuItem loadAll = MenuItemExt.newItem("加载全部", new SVGGlyph("/font/reload time.svg", "12"), "加载此zk节点全部子节点", this::loadChildAll);
                MenuItem expandAll = MenuItemExt.newItem("展开全部", new SVGGlyph("/font/colum-height.svg", "12"), "展开此zk节点全部子节点", this::expandAll);
                MenuItem collapseAll = MenuItemExt.newItem("收缩全部", new SVGGlyph("/font/vertical-align-middl.svg", "12"), "收缩此zk节点全部子节点", this::collapseAll);
                items.add(reload);
                items.add(loadAll);
                items.add(expandAll);
                items.add(collapseAll);
            }
            if (this.value.hasReadPerm()) {
                MenuItem export = MenuItemExt.newItem("导出节点", new SVGGlyph("/font/export.svg", "12"), "导出此zk节点及子节点数据", this::exportNode);
                items.add(export);
            }
            items.add(auth);
        }
        return items;
    }

    /**
     * 取消操作
     */
    public void cancel() {
        this.canceled = true;
        this.stopWaiting();
    }

    /**
     * 添加zk子节点
     */
    public void addNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKNodeAddController.class, this.window());
        fxView.setProp("zkItem", this);
        fxView.setProp("zkClient", this.client());
        fxView.display();
    }

    /**
     * 认证zk节点
     */
    public void authNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKAuthController.class, this.window());
        fxView.setProp("zkClient", this.client());
        fxView.setProp("zkItem", this);
        fxView.display();
    }

    /**
     * 导出zk节点
     */
    public void exportNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKNodeExportController.class, this.window());
        fxView.setProp("zkItem", this);
        fxView.setProp("zkClient", this.client());
        fxView.display();
    }

    @Override
    public void rename() {
        // 判断是否符合要求
        if (this.value.rootNode() || this.value.parentNode() || this.value.ephemeral()) {
            return;
        }
        String nodeName = MessageBox.prompt("请输入新的节点名称", this.value.nodeName());
        // 名称为空或名称跟当前名称相同，则忽略
        if (StrUtil.isBlank(nodeName) || Objects.equals(nodeName, this.value.nodeName())) {
            return;
        }
        // 检查是否存在
        String parentPath = ZKNodeUtil.getParentPath(this.value.nodePath());
        String newNodePath = ZKNodeUtil.concatPath(parentPath, nodeName);
        try {
            if (this.client().exists(newNodePath)) {
                MessageBox.warn("此节点已存在！");
                return;
            }
            CreateMode createMode = this.value.ephemeral() ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT;
            List<ACL> aclList = new ArrayList<>();
            for (ZKACL zkacl : this.value.acl()) {
                aclList.add(new ACL(zkacl.getPerms(), zkacl.getId()));
            }
            // 创建新节点并删除旧节点
            if (this.client().create(newNodePath, this.data(), aclList, null, createMode, true) != null) {
                // 删除旧节点
                this._delete();
            } else {
                MessageBox.warn("修改节点名称失败！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, "修改节点名称异常！");
        }
    }

    @Override
    public void delete() {
        // 不能删除根节点
        if (this.value.rootNode()) {
            return;
        }
        // 父节点删除提示
        if (this.value.parentNode() && !MessageBox.confirm("删除" + this.value.decodeNodePath(), "确实删除节点及所有子节点？（此操作无法撤销！）")) {
            return;
        }
        // 子节点删除提示
        if (this.value.subNode() && !MessageBox.confirm("删除" + this.value.decodeNodePath(), "确定删除节点？")) {
            return;
        }
        // 创建任务
        Task task = TaskBuilder.newBuilder()
                .onStart(this::_delete)
                .onFinish(this::stopWaiting)
                .onError(MessageBox::exception)
                .onSuccess(() -> MessageBox.okToast("节点已删除"))
                .build();
        this.startWaiting(task);
    }

    /**
     * 删除节点实际业务
     */
    private void _delete() {
        try {
            // 执行删除
            this.client().delete(this.nodePath(), null, this.value.parentNode());
            // 刷新状态
            this.parent().refreshStat();
            this.remove();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 当前节点的父zk节点
     *
     * @return 父zk节点
     */
    public ZKNodeTreeItem parent() {
        if (this.getParent() instanceof ZKNodeTreeItem treeItem) {
            return treeItem;
        }
        return null;
    }

    /**
     * 加载全部
     */
    public void loadChildAll() {
        Task task = TaskBuilder.newBuilder()
                .onFinish(() -> {
                    this.stopWaiting();
                    this.flushValue();
                })
                .onStart(() -> this.loadChildes(true))
                .onError(ex -> MessageBox.exception(ex, "加载失败！"))
                .build();
        this.startWaiting(task);
    }

    /**
     * 收缩全部
     */
    public void collapseAll() {
        Task task = TaskBuilder.newBuilder()
                .onFinish(this::stopWaiting)
                .onStart(() -> this.collapseAll(this))
                .onSuccess(() -> this.getTreeView().select(this))
                .onError(ex -> MessageBox.exception(ex, "收缩失败！"))
                .build();
        this.startWaiting(task);
    }

    /**
     * 展开全部
     */
    public void expandAll() {
        Task task = TaskBuilder.newBuilder()
                .onFinish(this::stopWaiting)
                .onStart(() -> this.expandAll(this))
                .onSuccess(() -> this.getTreeView().select(this))
                .onError(ex -> MessageBox.exception(ex, "展开失败！"))
                .build();
        this.startWaiting(task);
    }

    /**
     * 获取zk节点
     *
     * @param path 路径
     * @return zk节点
     */
    public ZKNodeTreeItem getChild(String path) {
        if (StrUtil.isNotBlank(path) && !this.isChildEmpty()) {
            for (TreeItem<?> child : this.getRealChildren()) {
                if (child instanceof ZKNodeTreeItem treeItem) {
                    if (treeItem.decodeNodePath().equals(path)) {
                        return treeItem;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void remove() {
        // 取消选中
        this.getTreeView().clearSelection();
        // 获取父节点
        ZKNodeTreeItem parent = this.parent();
        // 删除节点
        if (parent != null) {
            // 待选中节点
            TreeItem<?> selectItem = null;
            // 如果是最后删除的节点或者当前节点被选中
            if (this.client().isLastDelete(this.nodePath()) || this.getTreeView().isSelected(this)) {
                // 如果下一个节点不为null，则选中下一个节点，否则选中此节点的父节点
                selectItem = this.nextSibling();
                selectItem = selectItem == null ? parent : selectItem;
            }
            // 取消此节点的收藏
            this.unCollect();
            // 移除此节点
            parent.removeChild(this);
            // 选中节点
            if (selectItem != null) {
                this.getTreeView().select(selectItem);
            }
            // 刷新父节点值
            parent.flushValue();
        } else {
            StaticLog.warn("remove fail, this.parent() is null.");
        }
    }

    /**
     * 添加zk子节点
     *
     * @param path zk节点路径
     */
    public void addChild(String path) {
        if (StrUtil.isNotBlank(path)) {
            ZKNode node = ZKNodeUtil.getNode(this.client(), path);
            if (node != null) {
                this.addChild(node);
            } else {
                StaticLog.warn("获取zk节点:{} 失败", path);
            }
        }
    }

    /**
     * 添加zk子节点
     *
     * @param node zk子节点
     */
    public void addChild(ZKNode node) {
        if (node != null) {
            this.addChild(new ZKNodeTreeItem(node, this.root));
        }
    }

    /**
     * 刷新zk节点
     */
    public void refreshNode() {
        ZKNodeUtil.refreshNode(this.client(), this.value);
        this.clearStatus();
    }

    /**
     * 刷新zk节点数据
     */
    public void refreshData() throws Exception {
        StaticLog.debug("refreshData.");
        ZKNodeUtil.refreshData(this.client(), this.value);
        // 清空未保存的数据
        this.clearData();
        // 清空修改数据
        this.clearStatus();
    }

    /**
     * 刷新zk节点权限
     */
    public void refreshACL() throws Exception {
        StaticLog.debug("refreshACL.");
        this.value.acl(this.client().getACL(this.nodePath()));
        // 刷新图标
        this.flushGraphic();
    }

    /**
     * 刷新zk节点配额
     */
    public void reloadQuota() throws Exception {
        StatsTrack track = this.client().listQuota(this.nodePath());
        this.value.quota(track);
    }

    /**
     * 刷新zk节点状态
     */
    public void refreshStat() throws Exception {
        ZKNodeUtil.refreshStat(this.client(), this.value);
    }

    /**
     * 保存节点数据
     *
     * @return 结果
     */
    public boolean saveData() {
        if (this.dataUnsaved()) {
            try {
                byte[] data = this.data();
                // 更新数据
                Stat stat = this.client().setData(this.nodePath(), data);
                if (stat != null) {
                    // 更新数据
                    this.value.stat(stat);
                    this.value.nodeData(data);
                    // 清理属性
                    this.clearStatus();
                    this.clearData();
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            }
            return false;
        }
        return true;
    }

    /**
     * 是否能加载子节点
     *
     * @return 结果
     */
    private boolean canLoadSub() {
        return this.root.isConnected() && !this.loaded && this.value.parentNode() && this.value.hasReadPerm();
    }

    @Override
    public void reloadChild() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    this.loadChildes(false);
                    this.reExpanded();
                    this.refreshNode();
                })
                .onError(MessageBox::exception)
                .onFinish(this::stopWaiting)
                .build();
        this.startWaiting(task);
    }

    /**
     * 加载子节点
     *
     * @param loop 递归加载
     */
    public void loadChildes(boolean loop) {
        if (this.canceled) {
            return;
        }
        this.loaded = true;
        this.loading = true;
        try {
            // 没有子节点
            if (!this.value.hasChildren()) {
                this.clearChild();
            } else {
                // 获取节点列表
                List<ZKNode> list = ZKNodeUtil.getChildNode(this.client(), this.nodePath());
                // 添加列表
                List<TreeItem<?>> addList = new ArrayList<>(list.size());
                // 移除列表
                List<TreeItem<?>> delList = new ArrayList<>(list.size());
                // 遍历列表寻找待更新或者待添加节点
                f1:
                for (ZKNode node : list) {
                    // 判断节点是否存在
                    if (!this.isChildEmpty()) {
                        for (ZKNodeTreeItem item : this.showChildren()) {
                            if (StrUtil.equals(item.nodePath(), node.nodePath())) {
                                item.value.copy(node);
                                continue f1;
                            }
                        }
                    }
                    addList.add(new ZKNodeTreeItem(node, this.root));
                    // 预先加载一部分
                    if (addList.size() > 20) {
                        this.addChild(addList);
                        addList.clear();
                        // 根据递归深度判断是否当前节点
                        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                        int depth = 0;
                        for (StackTraceElement e : stackTrace) {
                            if (e.getMethodName().equals("loadChildes")) {
                                depth++;
                            }
                            if (depth > 1) {
                                break;
                            }
                        }
                        // 是当前节点，则展开
                        if (depth <= 1) {
                            this.extend();
                        }
                    }
                }
                // 遍历列表寻找待删除节点
                for (ZKNodeTreeItem item : this.showChildren()) {
                    // 判断节点是否不存在
                    if (list.parallelStream().noneMatch(node -> StrUtil.equals(item.nodePath(), node.nodePath()))) {
                        delList.add(item);
                    }
                }
                // 删除节点
                if (!delList.isEmpty()) {
                    this.removeChild(delList);
                }
                // 添加节点
                if (!addList.isEmpty()) {
                    this.addChild(addList);
                }
            }
            // 递归处理
            if (loop && !this.isChildEmpty()) {
                for (ZKNodeTreeItem item : this.showChildren()) {
                    if (this.canceled && item.canceled) {
                        break;
                    }
                    item.loadChildes(true);
                }
            }
        } catch (Exception ex) {
            this.loaded = false;
            throw new RuntimeException(ex);
        } finally {
            this.loading = false;
        }
    }

    /**
     * 显示的节点内容
     *
     * @return 节点内容
     */
    public List<ZKNodeTreeItem> showChildren() {
        return (List) super.getRealChildren();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ZKNodeTreeItem item) {
            return Comparator.comparing(ZKNodeTreeItem::nodePath).compare(this, item);
        }
        return super.compareTo(o);
    }

    @Override
    public void sortAsc() {
        if (super.isSortable()) {
            super.sortAsc();
            this.showChildren().forEach(ZKNodeTreeItem::sortAsc);
        }
    }

    // @Override
    // protected int sortAsc(RichTreeItem<?> item1, RichTreeItem<?> item2) {
    //     if (item1 instanceof ZKNodeTreeItem node1 && item2 instanceof ZKNodeTreeItem node2) {
    //         return Comparator.comparing(ZKNodeTreeItem::nodePath).compare(node1, node2);
    //     }
    //     return super.sortAsc(item1, item2);
    // }

    @Override
    public void sortDesc() {
        if (super.isSortable()) {
            super.sortDesc();
            this.showChildren().forEach(ZKNodeTreeItem::sortDesc);
        }
    }

    // @Override
    // protected int sortDesc(RichTreeItem<?> item1, RichTreeItem<?> item2) {
    //     if (item1 instanceof ZKNodeTreeItem node1 && item2 instanceof ZKNodeTreeItem node2) {
    //         return Comparator.comparing(ZKNodeTreeItem::nodePath).compare(node2, node1);
    //     }
    //     return super.sortDesc(item1, item2);
    // }

    /**
     * 是否需要认证
     *
     * @return 结果
     */
    public boolean needAuth() {
        if (ZKAuthUtil.isNeedAuth(this.value, this.client())) {
            return true;
        }
        return this.graphic().getUrl().contains("lock");
    }

    /**
     * 节点是否被收藏
     */
    public boolean isCollect() {
        return this.info().isCollect(this.nodePath());
    }

    /**
     * 收藏节点
     */
    public void collect() {
        this.info().addCollect(this.nodePath());
        ZKInfoStore.INSTANCE.update(this.info());
    }

    /**
     * 取消收藏节点
     */
    public void unCollect() {
        if (this.info().removeCollect(this.nodePath())) {
            ZKInfoStore.INSTANCE.update(this.info());
        }
    }

    /**
     * zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.root().value();
    }

    /**
     * 数据是否太大
     *
     * @return 结果
     */
    public boolean isDataTooLong() {
        return this.dataUnsaved() && this.data().length > 1024 * 1024;
    }

    /**
     * 解码的节点路径
     *
     * @return 解码的节点路径
     */
    public String decodeNodePath() {
        return this.value.decodeNodePath();
    }

    /**
     * 删除权限
     *
     * @param acl 权限
     * @return 当前状态
     * @throws Exception 异常
     */
    public Stat deleteACL(ZKACL acl) throws Exception {
        return this.client().deleteACL(this.nodePath(), acl);
    }

    /**
     * 获取权限
     *
     * @return 权限
     */
    public List<ZKACL> acl() {
        return this.value.acl();
    }

    /**
     * 友好状态信息
     *
     * @return 友好状态信息
     */
    public List<FriendlyInfo<Stat>> statInfos() {
        return this.value.statInfos();
    }

    /**
     * 是否有读取权限
     *
     * @return 结果
     */
    public boolean hasReadPerm() {
        return this.value.hasReadPerm();
    }

    /**
     * 是否临时节点
     *
     * @return 结果
     */
    public boolean ephemeral() {
        return this.value.ephemeral();
    }

    /**
     * acl是否为空
     *
     * @return 结果
     */
    public boolean aclEmpty() {
        return this.value.aclEmpty();
    }

    /**
     * 获取zk连接名称
     *
     * @return zk连接名称
     */
    public String infoName() {
        return this.info().getName();
    }

    /**
     * 解码的节点名称
     *
     * @return 解码的节点名称
     */
    public String decodeNodeName() {
        return this.value.decodeNodeName();
    }

    /**
     * 应用更改
     */
    public void applyUpdate() {
        try {
            this.value.nodeData(this.updateData);
            this.refreshStat();
            this.clearStatus();
            this.clearData();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取图标
     *
     * @return 图标
     */
    public SVGGlyph graphic() {
        return this.getValue().graphic();
    }

    /**
     * 获取配额
     *
     * @return 配额
     */
    public StatsTrack quota() {
        if (this.value.quota() == null) {
            try {
                this.reloadQuota();
            } catch (Exception ignored) {
            }
        }
        return this.value.quota();
    }

    /**
     * 保存配额
     *
     * @param bytes 配额数据大小
     * @param num   配额子节点数量
     * @throws Exception 异常
     */
    public void saveQuota(long bytes, int num) throws Exception {
        this.client().delQuota(this.nodePath(), true, true);
        this.client().createQuota(this.nodePath(), bytes, num);
    }

    /**
     * 清除子节点数量配额
     *
     * @throws Exception 异常
     */
    public void clearQuotaNum() throws Exception {
        this.client().delQuota(this.nodePath(), false, true);
    }

    /**
     * 清除节点数据配额
     *
     * @throws Exception 异常
     */
    public void clearQuotaBytes() throws Exception {
        this.client().delQuota(this.nodePath(), true, false);
    }

    /**
     * 是否存在摘要权限
     *
     * @param digest 摘要
     * @return 结果
     */
    public boolean existDigestACL(String digest) {
        return this.value.existDigestACL(digest);
    }

    /**
     * 是否有开放权限
     *
     * @return 结果
     */
    public boolean hasWorldACL() {
        return this.value.hasWorldACL();
    }

    /**
     * 是否存在IP权限
     *
     * @param ip ip内容
     * @return 结果
     */
    public boolean existIPACL(String ip) {
        return this.value.existIPACL(ip);
    }

    /**
     * 获取加载耗时
     *
     * @return 加载耗时
     */
    public short loadTime() {
        return this.value.loadTime() == 0 ? 1 : this.value.loadTime();
    }
}
