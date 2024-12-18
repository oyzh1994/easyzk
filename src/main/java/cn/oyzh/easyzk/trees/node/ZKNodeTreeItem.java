package cn.oyzh.easyzk.trees.node;

import cn.oyzh.common.dto.FriendlyInfo;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.controller.auth.ZKAuthAuthController;
import cn.oyzh.easyzk.controller.data.ZKDataExportController;
import cn.oyzh.easyzk.controller.node.ZKNodeAddController;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKCollectJdbcStore;
import cn.oyzh.easyzk.store.ZKDataHistoryJdbcStore;
import cn.oyzh.easyzk.store.ZKSettingJdbcStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.svg.glyph.LockSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;
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
public class ZKNodeTreeItem extends RichTreeItem<ZKNodeTreeItem.ZKNodeTreeItemValue> {

    /**
     * zk节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    protected ZKNode value;

    /**
     * 设置节点变更
     */
    public void setBeChanged() {
        this.bitValue().set(8, true);
        this.refresh();
    }

    /**
     * 是否变更
     *
     * @return 结果
     */
    public boolean isBeChanged() {
        return this.bitValue != null && this.bitValue().get(8);
    }

    /**
     * 设置节点删除
     */
    public void setBeDeleted() {
        this.bitValue().set(9, true);
        this.refresh();
    }

    /**
     * 是否删除
     *
     * @return 结果
     */
    public boolean isBeDeleted() {
        return this.bitValue != null && this.bitValue().get(9);
    }

    /**
     * 设置节点变化
     */
    public void setBeChildChanged() {
        this.bitValue().set(10, true);
        this.refresh();
    }

    /**
     * 是否子节点变化
     *
     * @return 结果
     */
    public boolean isBeChildChanged() {
        return this.bitValue != null && this.bitValue().get(10);
    }

    /**
     * 清除子节点变化
     */
    public void clearBeChildChanged() {
        if (this.bitValue != null) {
            this.bitValue.set(10, false);
        }
    }

    /**
     * 设置忽略变化
     */
    public void doIgnoreChanged() {
        this.bitValue().set(11, true);
    }

    /**
     * 是否忽略变化
     *
     * @return 结果
     */
    public boolean isIgnoreChanged() {
        return this.bitValue != null && this.bitValue().get(11);
    }

    /**
     * 设置忽略删除
     */
    public void doIgnoreDeleted() {
        this.bitValue().set(12, true);
    }

    /**
     * 是否忽略删除
     *
     * @return 结果
     */
    public boolean isIgnoreDeleted() {
        return this.bitValue != null && this.bitValue().get(12);
    }

    /**
     * 设置忽略子节点变化
     */
    public void doIgnoreChildChanged() {
        this.bitValue().set(13, true);
    }

    /**
     * 是否忽略子节点变化
     *
     * @return 结果
     */
    public boolean isIgnoreChildChanged() {
        return this.bitValue != null && this.bitValue().get(13);
    }

    /**
     * 设置是否需要认证
     *
     * @param needAuth 是否需要认证
     */
    public void setNeedAuth(boolean needAuth) {
        this.bitValue().set(14, needAuth);
    }

    /**
     * 是否需要认证
     *
     * @return 结果
     */
    public boolean isNeedAuth() {
        boolean needAuth = this.bitValue != null && this.bitValue().get(14);
        return needAuth || ZKAuthUtil.isNeedAuth(this.value, this.client());
    }

    /**
     * 设置已取消状态
     *
     * @param canceled 已取消状态
     */
    public void setCanceled(boolean canceled) {
        this.bitValue().set(15, canceled);
    }

    /**
     * 是否已取消
     *
     * @return 结果
     */
    public boolean isCanceled() {
        return this.bitValue != null && this.bitValue().get(15);
    }

    /**
     * 获取数据
     *
     * @return 数据
     */
    public byte[] nodeData() {
        return this.value.getNodeData();
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void nodeData(byte[] data) {
        this.value.setUnsavedData(data);
        this.refresh();
    }

    /**
     * 清理
     */
    public void clear() {
        this.value.clearUnsavedData();
    }

    /**
     * 数据是否未保存
     *
     * @return 结果
     */
    public boolean isDataUnsaved() {
        return this.value.hasUnsavedData();
    }

    public ZKNodeTreeItem(@NonNull ZKNode value, ZKNodeTreeView treeView) {
        super(treeView);
        this.value = value;
        this.setFilterable(true);
        this.setValue(new ZKNodeTreeItemValue(this));
    }

    /**
     * 获取节点路径
     *
     * @return 节点路径
     */
    public String nodePath() {
        return this.value.nodePath();
    }

    @Override
    public void loadChild() {
        if (!this.isWaiting() && !this.isLoaded() && !this.isLoading()) {
            this.loadChildAsync();
        }
    }

    /**
     * 加载子节点
     */
    private void loadChildAsync() {
        this.startWaiting(this::loadChildSync);
    }

    /**
     * 加载子节点
     */
    private void loadChildSync() {
        this.setLoaded(true);
        this.setLoading(true);
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> this.loadChild(false))
                .onSuccess(this::expend)
                .onFinish(() -> {
                    this.setLoading(false);
                    this.refresh();
                })
                .onError(ex -> {
                    this.setLoaded(false);
                    MessageBox.exception(ex);
                })
                .build();
        task.run();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isLoading()) {
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
                FXMenuItem loadAll = MenuItemHelper.loadAll("12", this::loadChildAllAsync);
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
        this.setCanceled(true);
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
        // StageAdapter fxView = StageManager.parseStage(ZKNodeExportController.class, this.window());
        // fxView.setProp("zkItem", this);
        // fxView.setProp("zkClient", this.client());
        StageAdapter fxView = StageManager.parseStage(ZKDataExportController.class);
        fxView.setProp("connect", this.connect());
        fxView.setProp("nodePath", this.nodePath());
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
            if (this.client().create(newNodePath, this.nodeData(), List.copyOf(this.value.acl()), null, createMode, true) != null) {
                // 删除旧节点
                this.deleteNode();
                // 发送事件
                ZKEventUtil.nodeAdded(this.connect(), newNodePath);
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
                .onError(MessageBox::exception)
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
        return (ZKNodeTreeItem) this.getParent();
    }

    /**
     * 取消加载
     */
    public void unloadChild() {
        this.clearChild();
        this.setLoaded(false);
    }

    /**
     * 加载全部
     */
    private void loadChildAllSync() {
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> this.loadChild(true))
                .onError(MessageBox::exception)
                .build();
        task.run();
    }

    /**
     * 加载全部
     */
    public void loadChildAllAsync() {
        this.startWaiting(this::loadChildAllSync);
    }

    /**
     * 收缩全部
     */
    public void collapseAll() {
        Task task = TaskBuilder.newBuilder()
                // .onFinish(this::stopWaiting)
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
                // .onFinish(this::stopWaiting)
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
            for (TreeItem<?> child : this.unfilteredChildren()) {
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
        // 获取父节点
        ZKNodeTreeItem parent = this.parent();
        // 删除节点
        if (parent != null) {
            // 下一个节点
            TreeItem<?> nextItem = null;
            // 如果当前节点被选中
            if (this.isSelected()) {
                // 如果下一个节点不为null，则选中下一个节点，否则选中此节点的父节点
                nextItem = this.nextSibling();
                nextItem = nextItem == null ? parent : nextItem;
            }
            // 取消此节点的收藏
            this.unCollect();
            // 移除此节点
            parent.removeChild(this);
            try {
                // 刷新父节点状态
                parent.refreshStat();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
            // 选中节点
            if (nextItem != null) {
                TreeItem<?> finalNextItem = nextItem;
                FXUtil.runPulse(() -> parent.getTreeView().select(finalNextItem));
            }
        } else {
            JulLog.warn("remove fail, this.parent() is null.");
        }
    }

    /**
     * 添加zk子节点
     *
     * @param path zk节点路径
     */
    public void addChild(String path) throws Exception {
        if (StringUtil.isNotBlank(path)) {
            ZKNode node = ZKNodeUtil.getNode(this.client(), path);
            this.addChild(node);
        }
    }

    /**
     * 添加zk子节点
     *
     * @param node zk子节点
     */
    public void addChild(ZKNode node) {
        if (node != null) {
            this.addChild(new ZKNodeTreeItem(node, this.getTreeView()));
        }
    }

    @Override
    public ZKNodeTreeView getTreeView() {
        return (ZKNodeTreeView) super.getTreeView();
    }

    public ZKClient client() {
        return this.getTreeView().client();
    }

    public ZKConnect connect() {
        return this.getTreeView().connect();
    }

    /**
     * 刷新zk节点
     */
    public void refreshNode() throws Exception {
        try {
            ZKNodeUtil.refreshNode(this.client(), this.value);
            this.clear();
        } catch (KeeperException.NoAuthException ex) {
            this.setNeedAuth(true);
        }
    }

    /**
     * 刷新zk节点数据
     */
    public void refreshData() throws Exception {
        try {
            JulLog.debug("refreshData.");
            ZKNodeUtil.refreshData(this.client(), this.value);
            this.clear();
        } catch (KeeperException.NoAuthException ex) {
            this.setNeedAuth(true);
        }
    }

    /**
     * 刷新zk节点权限
     */
    public void refreshACL() throws Exception {
        try {
            JulLog.debug("refreshACL.");
            ZKNodeUtil.refreshAcl(this.client(), this.value);
        } catch (KeeperException.NoAuthException ex) {
            this.setNeedAuth(true);
        }
    }

    /**
     * 刷新zk节点配额
     */
    public void refreshQuota() throws Exception {
        try {
            JulLog.debug("refreshQuota.");
            ZKNodeUtil.refreshQuota(this.client(), this.value);
        } catch (KeeperException.NoNodeException ignored) {
            this.value.quota(null);
        } catch (KeeperException.NoAuthException ex) {
            this.setNeedAuth(true);
        }
    }

    /**
     * 刷新zk节点状态
     */
    public void refreshStat() throws Exception {
        try {
            JulLog.debug("refreshStat.");
            ZKNodeUtil.refreshStat(this.client(), this.value);
            this.refresh();
        } catch (KeeperException.NoAuthException ex) {
            this.setNeedAuth(true);
        }
    }

    /**
     * 保存节点数据
     *
     * @return 结果
     */
    public boolean saveData() {
        try {
            // 当前数据
            byte[] data = this.unsavedData();
            // 更新数据
            Stat stat = this.client().setData(this.nodePath(), data);
            if (stat != null) {
                // 更新数据
                this.value.stat(stat);
                this.value.setNodeData(data);
                this.saveHistory();
                this.clear();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        } finally {
            this.refresh();
        }
        return false;
    }

    @Override
    public void reloadChild() {
        if (!this.isWaiting() || !this.isLoading()) {
            try {
                this.refreshNode();
                this.loadChildAsync();
            } catch (Exception ex) {
                MessageBox.exception(ex);
            }
        }
    }

    /**
     * 加载子节点
     *
     * @param loop 递归加载
     */
    public void loadChild(boolean loop) {
        if (this.isCanceled()) {
            return;
        }
        try {
            this.setLoaded(true);
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
                    for (ZKNodeTreeItem item : this.itemChildren()) {
                        if (item.nodeEquals(node)) {
                            item.copy(node);
                            continue f1;
                        }
                    }
                    // 添加到集合
                    addList.add(new ZKNodeTreeItem(node, this.getTreeView()));
                    // 预先加载一部分
                    if (addList.size() > 20 && !loadPre) {
                        this.addChild(addList);
                        this.expend();
                        addList.clear();
                        loadPre = true;
                    }
                }
                // 遍历列表寻找待删除节点
                for (ZKNodeTreeItem item : this.itemChildren()) {
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
                for (ZKNodeTreeItem item : this.itemChildren()) {
                    if (this.isCanceled() && item.isCanceled()) {
                        break;
                    }
                    item.loadChild(true);
                }
            }
        } catch (KeeperException.NoAuthException ex) {
            this.setLoaded(false);
            this.setNeedAuth(true);
        } catch (Exception ex) {
            // 非取消、连接关闭情况下，则抛出异常
            if (!this.isCanceled() && !this.client().isConnected()) {
                throw new RuntimeException(ex);
            }
            this.setSorting(false);
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
    public List<ZKNodeTreeItem> itemChildren() {
        return (List) super.unfilteredChildren();
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
            this.itemChildren().forEach(ZKNodeTreeItem::sortAsc);
        }
    }

    @Override
    public void sortDesc() {
        if (super.isSortable()) {
            super.sortDesc();
            this.itemChildren().forEach(ZKNodeTreeItem::sortDesc);
        }
    }

    /**
     * 节点是否被收藏
     */
    public boolean isCollect() {
        return ZKCollectJdbcStore.INSTANCE.exist(this.connect().getId(), this.decodeNodePath());
    }

    /**
     * 收藏节点
     */
    public void collect() {
        ZKCollectJdbcStore.INSTANCE.replace(this.connect().getId(), this.decodeNodePath());
    }

    /**
     * 取消收藏节点
     */
    public void unCollect() {
        ZKCollectJdbcStore.INSTANCE.delete(this.connect().getId(), this.decodeNodePath());
    }

    /**
     * 数据是否太大
     *
     * @return 结果
     */
    public boolean isDataTooLong() {
        if (this.isDataUnsaved()) {
            return this.value.getUnsavedDataSize() > 1024 * 1024;
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
    public String connectName() {
        return this.connect().getName();
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
        if (bytes == -1 && count == -1) {
            this.client().delQuota(this.nodePath(), true, true);
        } else {
            this.client().delQuota(this.nodePath(), true, true);
            this.client().createQuota(this.nodePath(), bytes, count);
        }
        this.refreshQuota();
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
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    /**
     * 保存数据历史
     */
    private void saveHistory() {
        ZKDataHistory history = new ZKDataHistory();
        history.setData(this.unsavedData());
        history.setPath(this.nodePath());
        history.setInfoId(this.connect().getId());
        ZKDataHistoryJdbcStore.INSTANCE.replace(history, this.client());
        ZKEventUtil.dataHistoryAdded(history, this);
    }

    public Integer getNumChildren() {
        return this.value.getNumChildren();
    }

    /**
     * 授权变化事件
     */
    public void authChanged() throws Exception {
        this.setNeedAuth(false);
        this.refreshNode();
        this.loadRoot();
        this.refresh();
    }

    /**
     * 加载根节点
     */
    public void loadRoot() {
        if (this.isRoot()) {
            ZKSetting setting = ZKSettingJdbcStore.SETTING;
            if (setting.isLoadFirst()) {
                this.loadChildSync();
            } else if (setting.isLoadAll()) {
                this.loadChildAllSync();
            }
        }
    }

    public byte[] unsavedData() {
        return this.value.getUnsavedData();
    }

    @Override
    public void destroy() {
        this.value.clearNodeData();
        this.value.clearUnsavedData();
        if (!this.isRoot()) {
            this.value = null;
            // this.bitValue = null;
            super.destroy();
        }
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKNodeTreeItemValue extends RichTreeItemValue {

        public ZKNodeTreeItemValue(@NonNull ZKNodeTreeItem item) {
            super(item);
        }

        @Override
        protected ZKNodeTreeItem item() {
            return (ZKNodeTreeItem) super.item();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic != null && this.graphic.isWaiting()) {
                return this.graphic;
            }
            boolean changed = false;
            if (this.graphic == null) {
                changed = true;
            } else if (this.item().isNeedAuth() && StringUtil.notEquals(this.graphic.getProp("_type"), "3")) {
                changed = true;
            } else if (this.item().isEphemeral() && StringUtil.notEquals(this.graphic.getProp("_type"), "2")) {
                changed = true;
            } else if (StringUtil.notEquals(this.graphic.getProp("_type"), "1")) {
                changed = true;
            }
            if (changed) {
                if (this.item().isNeedAuth()) {
                    this.graphic = new LockSVGGlyph("11");
                    this.graphic.setProp("_type", "3");
                } else if (this.item().isEphemeral()) {
                    this.graphic = new SVGGlyph("/font/temp.svg", 11);
                    this.graphic.setProp("_type", "2");
                } else {
                    this.graphic = new SVGGlyph("/font/file-text.svg", 11);
                    this.graphic.setProp("_type", "1");
                }
                this.graphic.disableTheme();
            }
            return super.graphic();
        }

        @Override
        public String extra() {
            String extra;
            int showNum = this.item().getChildrenSize();
            Integer totalNum = this.item().getNumChildren();
            if (totalNum == null || totalNum == 0) {
                extra = null;
            } else if (showNum == totalNum) {
                extra = "(" + totalNum + ")";
            } else {
                extra = "(" + showNum + "/" + totalNum + ")";
            }
            return extra;
        }

        @Override
        public Color graphicColor() {
            Color color;
            // 节点已删除
            if (this.item().isBeDeleted()) {
                color = Color.RED;
            } else if (this.item().isDataUnsaved()) { // 节点数据未保存
                color = Color.ORANGE;
            } else if (this.item().isBeChanged()) { // 节点已更新
                color = Color.PURPLE;
            } else if (this.item().isBeChildChanged()) {// 子节点已更新
                color = Color.BROWN;
            } else {
                color = super.graphicColor();
            }
            return color;
        }

        @Override
        public String name() {
            return this.item().decodeNodeName();
        }
    }
}
