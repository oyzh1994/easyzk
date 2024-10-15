package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.controller.auth.ZKAuthAuthController;
import cn.oyzh.easyzk.controller.node.ZKNodeAddController;
import cn.oyzh.easyzk.controller.node.ZKNodeExportController;
import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKCollectStore;
import cn.oyzh.easyzk.store.ZKDataHistoryStore2;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.dto.FriendlyInfo;
import cn.oyzh.fx.common.log.JulLog;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import cn.oyzh.fx.plus.trees.RichTreeView;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKNodeTreeItem extends ZKTreeItem<ZKNodeTreeItemValue> {

    /**
     * zk节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected ZKNode value;

    /**
     * 节点状态
     * 1. 已变更
     * 2. 已删除
     * 3. 子节点已变更
     */
    @Getter
    private Byte nodeStatus;

    /**
     * 忽略状态
     * 1. 已忽略变更
     * 2. 已忽略删除
     * 3. 已忽略子节点变更
     */
    private Byte ignoreStatus;

    /**
     * 取消标志位
     */
    private volatile boolean canceled;

    /**
     * 连接节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private ZKInfo info;

    @Getter
    @Accessors(fluent = true, chain = true)
    private ZKClient client;

    /**
     * 设置被删除状态
     */
    public void setBeChanged() {
        this.nodeStatus = 1;
        this.flushValue();
    }

    /**
     * 设置被删除状态
     */
    public void setBeDeleted() {
        this.nodeStatus = 2;
        this.flushValue();
    }

    /**
     * 设置被删除状态
     */
    public void setBeChildChanged() {
        this.nodeStatus = 3;
        this.flushValue();
    }

    public boolean isBeChanged() {
        return this.nodeStatus != null && this.nodeStatus == 1;
    }

    public boolean isBeDeleted() {
        return this.nodeStatus != null && this.nodeStatus == 2;
    }

    public boolean isBeChildChanged() {
        return this.nodeStatus != null && this.nodeStatus == 3;
    }

    /**
     * 设置被删除状态
     */
    public void doIgnoreChanged() {
        this.ignoreStatus = 1;
    }

    /**
     * 设置被删除状态
     */
    public void doIgnoreDeleted() {
        this.ignoreStatus = 2;
    }

    /**
     * 设置被删除状态
     */
    public void doIgnoreChildChanged() {
        this.ignoreStatus = 3;
    }

    public boolean isIgnoreChanged() {
        return this.ignoreStatus != null && this.ignoreStatus == 1;
    }

    public boolean isIgnoreDeleted() {
        return this.ignoreStatus != null && this.ignoreStatus == 2;
    }

    public boolean isIgnoreChildChanged() {
        return this.ignoreStatus != null && this.ignoreStatus == 3;
    }

    /**
     * 数据属性
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    private byte[] unsavedData;

    /**
     * 获取数据
     *
     * @return 数据
     */
    public byte[] nodeData() {
        return this.value.nodeData();
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void nodeData(byte[] data) {
        this.unsavedData = data;
        this.flushValue();
    }

    /**
     * 清理
     */
    public void clear() {
        this.nodeStatus = null;
        this.unsavedData = null;
        this.ignoreStatus = null;
        this.flushValue();
    }

    /**
     * 数据是否未保存
     *
     * @return 结果
     */
    public boolean isDataUnsaved() {
        return this.unsavedData != null;
    }

    public ZKNodeTreeItem(@NonNull ZKNode value, RichTreeView treeView, ZKClient client) {
        super(treeView);
        this.value = value;
        this.client = client;
        this.info = client.zkInfo();
        this.setFilterable(true);
        this.setValue(new ZKNodeTreeItemValue(this));
        // this.initValue();
        // this.flushValue();
        // if (this.value.isRoot()) {
        //     super.addEventHandler(treeNotificationEvent(), this.treeEventEventHandler());
        // } else {
        //     this.visibleProperty().addListener((observableValue, aBoolean, t1) -> super.addEventHandler(treeNotificationEvent(), this.treeEventEventHandler()));
        // }
    }

    // /**
    //  * 事件处理
    //  */
    // private EventHandler<TreeModificationEvent<ZKNodeTreeItem>> treeEventEventHandler = null;
    //
    // private EventHandler<TreeModificationEvent<ZKNodeTreeItem>> treeEventEventHandler() {
    //     if (this.treeEventEventHandler == null) {
    //         this.treeEventEventHandler = event -> {
    //             if (Objects.equals(this, event.getTreeItem())) {
    //                 if (event.getEventType() == branchCollapsedEvent()) {
    //                     this.clearChildValue();
    //                 } else if (event.getEventType() == branchExpandedEvent()) {
    //                     this.initChildValue();
    //                 } else if (event.getEventType() == childrenModificationEvent()) {
    //                     try {
    //                         // 添加、移除则刷新状态
    //                         if (event.wasAdded() || event.wasRemoved()) {
    //                             this.refreshStat();
    //                         }
    //                         ZKEventUtil.treeChildChanged();
    //                     } catch (Exception ex) {
    //                         ex.printStackTrace();
    //                     }
    //                 }
    //             }
    //         };
    //     }
    //     return this.treeEventEventHandler;
    // }

    // /**
    //  * 初始化值
    //  */
    // protected void initValue() {
    //     try {
    //         if (this.getValue().isChildEmpty()) {
    //             this.getValue().flush();
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    // /**
    //  * 销毁值
    //  */
    // protected void clearValue() {
    //     try {
    //         if (!this.getValue().isChildEmpty()) {
    //             this.getValue().clearChild();
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    // /**
    //  * 初始化子节点值
    //  */
    // protected void initChildValue() {
    //     try {
    //         for (ZKNodeTreeItem showChild : this.showChildren()) {
    //             showChild.initValue();
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    // /**
    //  * 销毁子节点值
    //  */
    // protected void clearChildValue() {
    //     try {
    //         for (ZKNodeTreeItem showChild : this.showChildren()) {
    //             showChild.clearValue();
    //         }
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

    @Override
    public void doFilter(RichTreeItemFilter itemFilter) {
        super.doFilter(itemFilter);
        this.flushValue();
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
        if (!this.isWaiting() && !this.loaded && !this.loading) {
            this.loadChildAsync();
        }
    }

    /**
     * 加载子节点
     */
    private void loadChildAsync() {
        this.loading = true;
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> this.loadChild(false))
                .onSuccess(this::extend)
                .onFinish(() -> {
                    this.loading = false;
                    this.flushValue();
                    this.stopWaiting();
                })
                .onError(MessageBox::exception)
                .build();
        this.startWaiting(task);
    }

    // @Override
    // public void free() {
    //     if (!this.loaded) {
    //         super.free();
    //     } else {
    //         this.loadChild();
    //     }
    // }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.loading) {
            FXMenuItem cancel = MenuItemHelper.cancelOperation("11", this::cancel);
            items.add(cancel);
        } else {
            // 持久节点
            if (this.isPersistent()) {
                FXMenuItem add = MenuItemHelper.addNode("12", this::addNode);
                items.add(add);
            }
            // 非根节点 + 子节点 + 持久节点
            if (!this.value.isRoot() && this.value.isChildren() && this.isPersistent()) {
                FXMenuItem rename = MenuItemHelper.renameNode("12", this::rename);
                items.add(rename);
            }
            // 非根节点
            if (!this.value.isRoot()) {
                FXMenuItem delete = MenuItemHelper.deleteNode("12", this::delete);
                items.add(delete);
            }
            // 重载
            FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
            items.add(reload);
            // 父节点
            if (this.value.isParent()) {
                FXMenuItem unload = MenuItemHelper.unload("12", this::unloadChild);
                FXMenuItem loadAll = MenuItemHelper.loadAll("12", this::loadChildAll);
                FXMenuItem expandAll = MenuItemHelper.expandAll("12", this::expandAll);
                FXMenuItem collapseAll = MenuItemHelper.collapseAll("12", this::collapseAll);
                items.add(unload);
                items.add(loadAll);
                items.add(expandAll);
                items.add(collapseAll);
            }
            // 持久节点 + 有读取权限
            if (this.isPersistent() && this.value.hasReadPerm()) {
                FXMenuItem export = MenuItemHelper.exportData("12", this::exportData);
                items.add(export);
            }
            // 认证
            FXMenuItem auth = MenuItemHelper.authNode("12", this::authNode);
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
        StageAdapter fxView = StageManager.parseStage(ZKNodeAddController.class, this.window());
        fxView.setProp("zkItem", this);
        fxView.setProp("zkClient", this.client());
        fxView.display();
    }

    /**
     * 认证zk节点
     */
    public void authNode() {
        StageAdapter fxView = StageManager.parseStage(ZKAuthAuthController.class, this.window());
        fxView.setProp("zkClient", this.client());
        fxView.setProp("zkItem", this);
        fxView.display();
    }

    /**
     * 导出zk节点
     */
    public void exportData() {
        StageAdapter fxView = StageManager.parseStage(ZKNodeExportController.class, this.window());
        fxView.setProp("zkItem", this);
        fxView.setProp("zkClient", this.client());
        fxView.display();
    }

    @Override
    public void rename() {
        // 判断是否符合要求
        if (this.isRoot() || this.value.isParent() || this.value.isEphemeral()) {
            return;
        }
        String nodeName = MessageBox.prompt(I18nHelper.pleaseInputNodeName(), this.value.nodeName());
        // 名称为空或名称跟当前名称相同，则忽略
        if (StringUtil.isBlank(nodeName) || Objects.equals(nodeName, this.value.nodeName())) {
            return;
        }
        // 检查是否存在
        String parentPath = ZKNodeUtil.getParentPath(this.value.nodePath());
        String newNodePath = ZKNodeUtil.concatPath(parentPath, nodeName);
        try {
            if (this.client().exists(newNodePath)) {
                MessageBox.warn(I18nHelper.node() + " [" + newNodePath + "] " + I18nHelper.alreadyExists());
                return;
            }
            // 创建模式
            CreateMode createMode = this.value.isEphemeral() ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT;
            // 创建新节点
            if (this.client.create(newNodePath, this.nodeData(), List.copyOf(this.value.acl()), null, createMode, true) != null) {
                // 删除旧节点
                this.deleteNode();
                // 发送事件
                ZKEventUtil.nodeAdd(this.info, newNodePath);
            } else {// 操作失败
                MessageBox.warn(I18nHelper.operationFail());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    @Override
    public void delete() {
        // 根节点
        if (this.isRoot()) {
            return;
        }
        // 父节点
        if (!MessageBox.confirm(I18nHelper.delete() + " [" + this.value.decodeNodePath() + "] ", I18nHelper.areYouSure())) {
            return;
        }
        // 创建任务
        Task task = TaskBuilder.newBuilder()
                .onStart(this::deleteNode)
                .onFinish(this::stopWaiting)
                .onError(MessageBox::exception)
                // .onSuccess(() -> MessageBox.okToast(I18nHelper.operationSuccess()))
                .build();
        this.startWaiting(task);
    }

    /**
     * 删除节点
     */
    private void deleteNode() throws Exception {
        try {
            ZKNodeTreeItem parent = this.parent();
            // 执行删除
            this.client().delete(this.nodePath(), null, this.value.isParent());
            // 刷新状态
            if (parent != null) {
                parent.refreshStat();
            }
        } catch (KeeperException.NoNodeException ignore) {

        }
        // 删除树节点
        this.remove();
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
     * 取消加载
     */
    public void unloadChild() {
        this.clearChild();
        this.loaded = false;
    }

    /**
     * 加载全部
     */
    public void loadChildAll() {
        Task task = TaskBuilder.newBuilder()
                .onFinish(this::stopWaiting)
                .onSuccess(this::flushValue)
                .onStart(() -> this.loadChild(true))
                .onError(ex -> MessageBox.exception(ex, I18nHelper.operationFail()))
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
                .onError(ex -> MessageBox.exception(ex, I18nHelper.operationFail()))
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
                .onError(ex -> MessageBox.exception(ex, I18nHelper.operationFail()))
                .build();
        this.startWaiting(task);
    }

    /**
     * 获取zk节点
     *
     * @param path 路径
     * @return zk节点
     */
    public ZKNodeTreeItem getNodeItem(String path) {
        if (StringUtil.isNotBlank(path) && !this.isChildEmpty()) {
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
        // // 取消选中
        // this.getTreeView().clearSelection();
        // 获取父节点
        ZKNodeTreeItem parent = this.parent();
        // 删除节点
        if (parent != null) {
            // 待选中节点
            TreeItem<?> selectItem = null;
            // 如果是最后删除的节点或者当前节点被选中
            if (this.client().isLastDelete(this.nodePath()) || this.isSelected()) {
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
            } else {// 清除选择
                this.getTreeView().clearSelection();
            }
            // 刷新父节点值
            parent.flushValue();
        } else {
            JulLog.warn("remove fail, this.parent() is null.");
        }
    }

    /**
     * 添加zk子节点
     *
     * @param path zk节点路径
     */
    public void addChild(String path) {
        if (StringUtil.isNotBlank(path)) {
            ZKNode node = ZKNodeUtil.getNode(this.client(), path);
            if (node != null) {
                this.addChild(node);
            } else {
                JulLog.warn("获取zk节点:{} 失败", path);
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
            this.addChild(new ZKNodeTreeItem(node, this.getTreeView(), this.client));
        }
    }

    @Override
    public ZKNodeTreeView getTreeView() {
        return (ZKNodeTreeView) super.getTreeView();
    }

    /**
     * 刷新zk节点
     */
    public void refreshNode() {
        ZKNodeUtil.refreshNode(this.client(), this.value);
        this.clear();
    }

    /**
     * 刷新zk节点数据
     */
    public void refreshData() throws Exception {
        JulLog.debug("refreshData.");
        ZKNodeUtil.refreshData(this.client(), this.value);
        this.clear();
    }

    /**
     * 刷新zk节点权限
     */
    public void refreshACL() throws Exception {
        JulLog.debug("refreshACL.");
        ZKNodeUtil.refreshAcl(this.client(), this.value);
    }

    /**
     * 刷新zk节点配额
     */
    public void refreshQuota() throws Exception {
        JulLog.debug("refreshQuota.");
        ZKNodeUtil.refreshQuota(this.client(), this.value);
    }

    /**
     * 刷新zk节点状态
     */
    public void refreshStat() throws Exception {
        JulLog.debug("refreshStat.");
        ZKNodeUtil.refreshStat(this.client(), this.value);
    }

    /**
     * 保存节点数据
     *
     * @return 结果
     */
    public boolean saveData() {
        try {
            // 保存数据历史
            this.saveDataHistory();
            // 当前数据
            byte[] data = this.unsavedData;
            // 更新数据
            Stat stat = this.client().setData(this.nodePath(), data);
            if (stat != null) {
                // 更新数据
                this.value.stat(stat);
                this.value.nodeData(data);
                this.clear();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void reloadChild() {
        if (!this.isWaiting() || !this.loading) {
            this.refreshNode();
            this.loadChildAsync();
        }
    }

    /**
     * 加载子节点
     *
     * @param loop 递归加载
     */
    public void loadChild(boolean loop) {
        if (this.canceled) {
            return;
        }
        try {
            this.loaded = true;
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
                // 预加载标志位
                boolean loadPre = false;
                // 遍历列表寻找待更新或者待添加节点
                f1:
                for (ZKNode node : list) {
                    // 判断节点是否存在
                    for (ZKNodeTreeItem item : this.showChildren()) {
                        if (item.nodeEquals(node)) {
                            item.copy(node);
                            continue f1;
                        }
                    }
                    // 添加到集合
                    addList.add(new ZKNodeTreeItem(node, this.getTreeView(), this.client));
                    // 预先加载一部分
                    if (addList.size() > 20 && !loadPre) {
                        this.addChild(addList);
                        this.extend();
                        addList.clear();
                        loadPre = true;
                    }
                }
                // 遍历列表寻找待删除节点
                for (ZKNodeTreeItem item : this.showChildren()) {
                    // 判断节点是否不存在
                    if (list.parallelStream().noneMatch(item::nodeEquals)) {
                        delList.add(item);
                    }
                }
                // 删除节点
                this.removeChild(delList);
                // 添加节点
                this.addChild(addList);
            }
            // 递归处理
            if (loop && !this.isChildEmpty()) {
                for (ZKNodeTreeItem item : this.showChildren()) {
                    if (this.canceled && item.canceled) {
                        break;
                    }
                    item.loadChild(true);
                }
            }
        } catch (Exception ex) {
            // 非取消、连接关闭情况下，则抛出异常
            if (!this.canceled && !this.client.isConnected()) {
                throw new RuntimeException(ex);
            }
            this.loaded = false;
        }
    }

    /**
     * 复制节点
     *
     * @param node zk节点
     */
    public void copy(ZKNode node) {
        this.value.copy(node);
    }

    /**
     * 节点比较
     *
     * @param node 目标节点
     * @return 结果
     */
    public boolean nodeEquals(ZKNode node) {
        return this.value.nodeEquals(node);
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

    @Override
    public void sortDesc() {
        if (super.isSortable()) {
            super.sortDesc();
            this.showChildren().forEach(ZKNodeTreeItem::sortDesc);
        }
    }

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
        // return this.info().isCollect(this.nodePath());
        return ZKCollectStore.INSTANCE.exist(this.info().getId(), this.nodePath());
    }

    /**
     * 收藏节点
     */
    public void collect() {
        this.info().addCollect(this.nodePath());
        // ZKInfoStore.INSTANCE.update(this.info());
        ZKCollectStore.INSTANCE.replace(this.info().getId(), this.nodePath());
    }

    /**
     * 取消收藏节点
     */
    public void unCollect() {
        // if (this.info().removeCollect(this.nodePath())) {
        //     ZKInfoStore.INSTANCE.update(this.info());
        ZKCollectStore.INSTANCE.delete(this.info().getId(), this.nodePath());
        // }
    }

    // /**
    //  * zk信息
    //  *
    //  * @return zk信息
    //  */
    // public ZKInfo info() {
    //     return this.root().value();
    // }

    /**
     * 数据是否太大
     *
     * @return 结果
     */
    public boolean isDataTooLong() {
        if (this.isDataUnsaved()) {
            return this.unsavedData.length > 1024 * 1024;
        }
        return this.nodeData().length > 1024 * 1024;
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
     * 是否持久节点
     *
     * @return 结果
     */
    public boolean isPersistent() {
        return this.value.isPersistent();
    }

    /**
     * 是否临时节点
     *
     * @return 结果
     */
    public boolean isEphemeral() {
        return this.value.isEphemeral();
    }

    /**
     * 是否临时节点
     *
     * @return 结果
     */
    public boolean isRoot() {
        return this.value.isRoot();
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

    // /**
    //  * 应用更改
    //  */
    // public void applyUpdate() {
    //     try {
    //         this.value.nodeData(this.updateData);
    //         this.refreshStat();
    //         this.clearStatus();
    //         this.clearData();
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //     }
    // }

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
    public StatsTrack quota() throws Exception {
        if (this.value.quota() == null) {
            this.refreshQuota();
        }
        return this.value.quota();
    }

    /**
     * 保存配额
     *
     * @param bytes 配额数据大小
     * @param count 配额子节点数量
     * @throws Exception 异常
     */
    public void saveQuota(long bytes, int count) throws Exception {
        this.client().delQuota(this.nodePath(), true, true);
        this.client().createQuota(this.nodePath(), bytes, count);
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

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.loaded) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    /**
     * 保存数据历史
     */
    public void saveDataHistory() {
        ZKDataHistory history = new ZKDataHistory();
        history.setData(this.unsavedData);
        history.setPath(this.nodePath());
        history.setInfoId(this.info().getId());
        ZKDataHistoryStore2.INSTANCE.replace(history, this.client());
        ZKEventUtil.dataHistoryAdded(history, this);
    }

    @Override
    public void destroy() {
        // if (this.treeEventEventHandler != null) {
        //     this.removeEventHandler(treeNotificationEvent(), this.treeEventEventHandler);
        //     this.treeEventEventHandler = null;
        // }
        this.info = null;
        this.value = null;
        this.client = null;
        super.destroy();
    }

    public Integer getNumChildren() {
        return this.value.getNumChildren();
    }
}
