package cn.oyzh.easyzk.fx;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
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
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Window;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author oyzh
 * @since 2023/1/30
 */
@Slf4j
public class ZKNodeTreeItem extends ZKTreeItem {

    /**
     * zk节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private ZKNode value;

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
     * 可见标志位
     */
    @Getter
    private volatile boolean visible = true;

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
        this.flushStatus();
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
        this.flushStatus();
    }

    /**
     * 清除数据状态
     */
    public void clearStatus() {
        this.beDeleted = false;
        this.updateData = null;
        this.flushStatus();
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
                this.value.nodeData(this.zkClient().getData(this.nodePath()));
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
    }

    /**
     * 设置数据
     *
     * @param data 数据
     */
    public void data(String data) {
        this.dataProperty().set(data.getBytes(this.charset));
    }

    /**
     * 清除数据
     */
    public void clearData() {
        if (this.dataProperty != null) {
            this.dataProperty.set(null);
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

    /**
     * 子节点数量属性
     */
    private SimpleIntegerProperty childNumProperty;

    /**
     * 获取子节点数量
     *
     * @return 子节点数量
     */
    public int childNum() {
        if (this.childNumProperty == null) {
            return -1;
        }
        return this.childNumProperty.get();
    }

    /**
     * 获取子节点数量属性
     *
     * @return 子节点数量属性
     */
    public SimpleIntegerProperty childNumProperty() {
        if (this.childNumProperty == null) {
            this.childNumProperty = new SimpleIntegerProperty();
        }
        return this.childNumProperty;
    }

    /**
     * 子节点列表，记录用，非实际展示列表
     */
    @Accessors(fluent = true, chain = true)
    private ObservableList<ZKNodeTreeItem> children;

    /**
     * 获取子节点列表
     *
     * @return 子节点列表
     */
    public ObservableList<ZKNodeTreeItem> children() {
        if (this.children == null) {
            // 创建集合
            this.children = FXCollections.observableArrayList();
            // 监听子节点变化
            this.children.addListener((ListChangeListener<ZKNodeTreeItem>) c -> {
                try {
                    c.next();
                    // 添加、替换，执行过滤
                    if (c.wasAdded() || c.wasReplaced()) {
                        this.doFilter(this.treeView().itemFilter());
                    }
                    // 添加、移除、替换
                    if (c.wasAdded() || c.wasRemoved() || c.wasReplaced()) {
                        // 刷新子节点
                        this.flushChildren();
                        // 刷新状态
                        this.refreshStat();
                    }
                    // 添加、替换，执行排序
                    if (c.wasAdded() || c.wasReplaced()) {
                        this.sort();
                        // this.sort(this.treeView().sortOrder());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
        return this.children;
    }

    public ZKNodeTreeItem(@NonNull ZKNode value, @NonNull ZKConnectTreeItem root) {
        this.root = root;
        this.treeView(root.treeView());
        this.value(value);
    }

    /**
     * 设置值
     *
     * @param value zk节点
     */
    public void value(@NonNull ZKNode value) {
        this.value = value;
        this.itemValue(new ZKNodeTreeItemValue(this));
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient zkClient() {
        return this.root.zkClient();
    }

    /**
     * 获取窗口
     *
     * @return 窗口
     */
    public Window window() {
        return this.treeView().window();
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
                    .onStart(() -> {
                        this.loadChildes(false);
                        this.extend();
                    })
                    .onFinish(this::stopWaiting)
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
            Task task = TaskBuilder.newBuilder().onStart(() -> {
                this.loadChildes(false);
                this.extend();
            }).build();
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
        return this.itemValue().getSVGUrl();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.loading) {
            MenuItem cancel = MenuItemExt.newItem("取消操作", new SVGGlyph("/font/close.svg", "11"), "取消操作", this::cancel);
            items.add(cancel);
        } else {
            MenuItem add = MenuItemExt.newItem("添加节点", new SVGGlyph("/font/add.svg", "12"), "添加zk子节点", this::addNode);
            MenuItem export = MenuItemExt.newItem("导出节点", new SVGGlyph("/font/export.svg", "12"), "导出此zk节点及子节点数据", this::exportNode);
            MenuItem rename = MenuItemExt.newItem("节点更名", new SVGGlyph("/font/edit-square.svg", "12"), "更改节点名称(快捷键f2)", this::rename);
            MenuItem delete = MenuItemExt.newItem("删除节点", new SVGGlyph("/font/delete.svg", "12"), "删除此zk节点及子节点(快捷键delete)", this::delete);
            MenuItem reload = MenuItemExt.newItem("重新载入", new SVGGlyph("/font/reload.svg", "12"), "重新加载此zk节点子节点", this::reloadChild);
            MenuItem loadAll = MenuItemExt.newItem("加载全部", new SVGGlyph("/font/reload time.svg", "12"), "加载此zk节点全部子节点", this::loadChildAll);
            MenuItem expandAll = MenuItemExt.newItem("展开全部", new SVGGlyph("/font/colum-height.svg", "12"), "展开此zk节点全部子节点", this::expandAll);
            MenuItem collapseAll = MenuItemExt.newItem("收缩全部", new SVGGlyph("/font/vertical-align-middl.svg", "12"), "收缩此zk节点全部子节点", this::collapseAll);
            MenuItem auth = MenuItemExt.newItem("认证节点", new SVGGlyph("/font/unlock.svg", "12"), "对zk节点进行认证", this::authNode);

            add.setDisable(this.value.ephemeral());
            rename.setDisable(this.value.rootNode() || this.value.parentNode() || this.value.ephemeral());
            delete.setDisable(this.value.rootNode());
            loadAll.setDisable(this.value.subNode());
            expandAll.setDisable(this.value.subNode());
            collapseAll.setDisable(this.value.subNode());
            export.setDisable(!this.value.hasReadPerm());

            items.add(add);
            items.add(rename);
            items.add(delete);
            items.add(export);
            items.add(reload);
            items.add(loadAll);
            items.add(expandAll);
            items.add(collapseAll);
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
        fxView.setProp("zkClient", this.zkClient());
        fxView.display();
    }

    /**
     * 认证zk节点
     */
    public void authNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKAuthController.class, this.window());
        fxView.setProp("zkClient", this.zkClient());
        fxView.setProp("zkItem", this);
        fxView.display();
    }

    /**
     * 导出zk节点
     */
    public void exportNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKNodeExportController.class, this.window());
        fxView.setProp("zkItem", this);
        fxView.setProp("zkClient", this.zkClient());
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
            if (this.zkClient().exists(newNodePath)) {
                MessageBox.warn("此节点已存在！");
                return;
            }
            CreateMode createMode = this.value.ephemeral() ? CreateMode.EPHEMERAL : CreateMode.PERSISTENT;
            List<ACL> aclList = new ArrayList<>();
            for (ZKACL zkacl : this.value.acl()) {
                aclList.add(new ACL(zkacl.getPerms(), zkacl.getId()));
            }
            // 创建新节点并删除旧节点
            if (this.zkClient().create(newNodePath, this.data(), aclList, null, createMode, true) != null) {
                // 删除旧节点
                this.deleteNode();
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

        Task task = new Task() {
            @Override
            public void onStart() throws Exception {
                deleteNode();
                MessageBox.okToast("节点已删除");
            }
        };
        task.setFinish(this::stopWaiting);
        task.setError(MessageBox::exception);
        this.startWaiting(task);
    }

    /**
     * 删除节点
     *
     * @throws Exception 异常
     */
    private void deleteNode() throws Exception {
        // 执行删除
        this.zkClient().delete(this.nodePath(), null, this.value.parentNode());
        // 取消选中
        this.treeView().clearSelection();
        // 移除节点
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
     * 加载全部
     */
    public void loadChildAll() {
        Task task = TaskBuilder.newBuilder()
                .onFinish(this::stopWaiting)
                .onStart(() -> this.loadChildes(true))
                .onSuccess(() -> this.treeView().select(this))
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
                .onSuccess(() -> this.treeView().select(this))
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
                .onSuccess(() -> this.treeView().select(this))
                .onError(ex -> MessageBox.exception(ex, "展开失败！"))
                .build();
        this.startWaiting(task);
    }


    /**
     * 收缩所有节点
     *
     * @param item 待收缩节点
     */
    private void collapseAll(ZKNodeTreeItem item) {
        item.setExpanded(false);
        if (!item.isChildEmpty()) {
            for (ZKNodeTreeItem treeItem : item.children) {
                this.collapseAll(treeItem);
            }
        }
    }

    /**
     * 展开所有节点
     *
     * @param item 待展开节点
     */
    private void expandAll(ZKNodeTreeItem item) {
        item.setExpanded(true);
        if (!item.isChildEmpty()) {
            for (ZKNodeTreeItem treeItem : item.children) {
                this.expandAll(treeItem);
            }
        }
    }

    /**
     * 刷新子节点列表
     */
    public void flushChildren() {
        if (this.isChildEmpty()) {
            super.getChildren().clear();
            return;
        }
        // 添加列表
        List<TreeItem<?>> addList = null;
        // 移除列表
        List<TreeItem<?>> removeList = null;
        // 显示列表
        ObservableList<TreeItem<?>> thatChildren = super.getChildren();
        // 遍历节点并处理
        for (ZKNodeTreeItem child : this.children()) {
            if (this.nodeVisible(child)) {
                if (!thatChildren.contains(child)) {
                    if (addList == null) {
                        addList = new ArrayList<>();
                    }
                    addList.add(child);
                }
                child.flushChildren();
            } else {
                if (removeList == null) {
                    removeList = new ArrayList<>();
                }
                removeList.add(child);
            }
        }
        // 移除和添加节点
        if (removeList != null && addList != null) {
            thatChildren.removeAll(removeList);
            thatChildren.addAll(addList);
        } else if (removeList != null) { // 移除节点
            thatChildren.removeAll(removeList);
        } else if (addList != null) {// 添加节点
            thatChildren.addAll(addList);
        }
        // 更新节点数量显示
        this.childNumProperty().set(thatChildren.size());
        // 触发节点变化事件
        ZKEventUtil.treeChildChanged();
    }

    /**
     * 节点是否可见
     *
     * @return 结果
     */
    public boolean nodeVisible() {
        return this.nodeVisible(this);
    }

    /**
     * 节点是否可见
     *
     * @param item 节点
     * @return 结果
     */
    public boolean nodeVisible(ZKNodeTreeItem item) {
        if (item == null) {
            return false;
        }
        if (item.visible) {
            return true;
        }
        if (item.isChildEmpty()) {
            return false;
        }
        return item.children.parallelStream().anyMatch(ZKNodeTreeItem::isVisible) || item.children.parallelStream().anyMatch(this::nodeVisible);
    }

    /**
     * 获取zk节点
     *
     * @param path 路径
     * @return zk节点
     */
    public ZKNodeTreeItem getChild(String path) {
        if (StrUtil.isNotBlank(path) && !this.isChildEmpty()) {
            List<ZKNodeTreeItem> childes = new CopyOnWriteArrayList<>(this.children);
            Optional<ZKNodeTreeItem> item = childes.parallelStream().filter(i -> i.value.decodeNodePath().equals(path)).findAny();
            return item.orElse(null);
        }
        return null;
    }

    @Override
    public void removeChild(@NonNull TreeItem<?> item) {
        if (!this.isChildEmpty()) {
            super.removeChild(item);
            this.children.remove(item);
        }
    }

    @Override
    public void removeChildes(@NonNull List<TreeItem<?>> items) {
        if (!this.isChildEmpty()) {
            super.removeChildes(items);
            this.children().removeAll(items);
        }
    }

    @Override
    public void remove() {
        ZKNodeTreeItem parent = this.parent();
        if (parent != null) {
            // 待选中节点
            TreeItem<?> selectItem = null;
            // 如果是最后删除的节点或者当前节点被选中
            if (this.zkClient().isLastDelete(this.nodePath()) || this.treeView().isSelected(this)) {
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
                this.treeView().select(selectItem);
            }
        } else {
            log.warn("remove fail, this.parent() is null.");
        }
    }

    @Override
    public boolean isChildEmpty() {
        if (this.children != null) {
            return this.children.isEmpty();
        }
        return true;
    }

    /**
     * 添加zk子节点
     *
     * @param path zk节点路径
     */
    public void addChild(String path) {
        if (StrUtil.isNotBlank(path)) {
            ZKNode node = ZKNodeUtil.getNode(this.zkClient(), path);
            if (node != null) {
                this.addChild(node);
            } else {
                log.warn("获取zk节点:{} 失败", path);
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

    @Override
    public void addChild(@NonNull TreeItem<?> item) {
        if (item instanceof ZKNodeTreeItem treeItem) {
            // 添加节点
            this.children().add(treeItem);
        }
    }

    /**
     * 添加zk子节点
     *
     * @param nodes 子节点
     */
    public void addNodes(List<ZKNode> nodes) {
        if (CollUtil.isNotEmpty(nodes)) {
            // 生成子节点
            List<ZKNodeTreeItem> childes = new ArrayList<>(nodes.size());
            nodes.forEach(s -> childes.add(new ZKNodeTreeItem(s, this.root)));
            // 设置子节点
            this.children().addAll(childes);
        }
    }

    /**
     * 替换zk子节点
     *
     * @param nodes 子节点
     */
    public void replaceNodes(List<ZKNode> nodes) {
        if (CollUtil.isNotEmpty(nodes)) {
            // 生成子节点
            List<ZKNodeTreeItem> childes = new ArrayList<>(nodes.size());
            nodes.forEach(s -> childes.add(new ZKNodeTreeItem(s, this.root)));
            // 设置子节点
            this.children().setAll(childes);
        } else {
            this.children.clear();
        }
    }

    /**
     * 刷新zk节点
     */
    public void refreshNode() {
        ZKNodeUtil.refreshNode(this.zkClient(), this.value);
        this.clearStatus();
    }

    /**
     * 刷新zk节点数据
     */
    public void refreshData() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("refreshData.");
        }
        ZKNodeUtil.refreshData(this.zkClient(), this.value);
        // 清空未保存的数据
        this.clearData();
        // 清空修改数据
        this.clearStatus();
    }

    /**
     * 刷新zk节点权限
     */
    public void refreshACL() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("refreshACL.");
        }
        this.value.acl(this.zkClient().getACL(this.nodePath()));
        // 刷新图标
        this.flushGraphic();
    }

    /**
     * 刷新zk节点配额
     */
    public void reloadQuota() throws Exception {
        StatsTrack track = this.zkClient().listQuota(this.nodePath());
        this.value.quota(track);
    }

    /**
     * 刷新zk节点状态
     */
    public void refreshStat() throws Exception {
        ZKNodeUtil.refreshStat(this.zkClient(), this.value);
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
                Stat stat = this.zkClient().setData(this.nodePath(), data);
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

    @Override
    public void doFilter(@NonNull RichTreeItemFilter filter) {
        if (!this.isChildEmpty()) {
            List<ZKNodeTreeItem> childes = new CopyOnWriteArrayList<>(this.children());
            for (ZKNodeTreeItem child : childes) {
                child.visible = filter.apply(child);
                child.doFilter(filter);
            }
        }
    }

    /**
     * 是否能加载子节点
     *
     * @return 结果
     */
    private boolean canLoadSub() {
        return this.root.isConnect() && !this.loaded && this.value.parentNode() && this.value.hasReadPerm();
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
            // 获取子节点列表
            List<String> subList = this.zkClient().getChildren(this.nodePath());
            if (CollUtil.isEmpty(subList)) {
                this.children().clear();
            } else {
                // 获取节点列表
                List<ZKNode> list = ZKNodeUtil.getChildNode(this.zkClient(), this.nodePath());
                // 覆盖
                if (this.isChildEmpty()) {
                    this.replaceNodes(list);
                } else {// 更新
                    // 添加列表
                    List<ZKNode> addList = new ArrayList<>();
                    // 移除列表
                    List<TreeItem<?>> delList = new ArrayList<>();
                    // 遍历列表寻找待更新或者待添加节点
                    f1:
                    for (ZKNode node : list) {
                        // 判断节点是否存在
                        for (ZKNodeTreeItem item : this.children()) {
                            if (StrUtil.equals(item.value.nodePath(), node.nodePath())) {
                                item.value.copy(node);
                                continue f1;
                            }
                        }
                        addList.add(node);
                    }
                    // 遍历列表寻找待删除节点
                    f1:
                    for (ZKNodeTreeItem item : this.children()) {
                        // 判断节点是否不存在
                        for (ZKNode node : list) {
                            if (StrUtil.equals(item.value.nodePath(), node.nodePath())) {
                                continue f1;
                            }
                        }
                        delList.add(item);
                    }
                    // 删除节点
                    if (!delList.isEmpty()) {
                        this.removeChildes(delList);
                    }
                    // 添加节点
                    if (!addList.isEmpty()) {
                        this.addNodes(addList);
                    }
                }
            }
            // 递归处理
            if (loop && !this.isChildEmpty()) {
                for (ZKNodeTreeItem child : this.children()) {
                    if (this.canceled || child.canceled) {
                        break;
                    }
                    child.loadChildes(true);
                }
            }
        } catch (Exception ex) {
            this.loaded = false;
            throw new RuntimeException(ex);
        } finally {
            this.loading = false;
        }
    }

    @Override
    public void sortAsc() {
        this.sortType = 0;
        if (!this.isChildEmpty()) {
            // 执行排序
            List<ZKNodeTreeItem> childes = this.getChildren();
            childes.sort(Comparator.comparing(ZKNodeTreeItem::nodePath));
            childes.forEach(ZKNodeTreeItem::sortAsc);
        }
    }

    @Override
    public void sortDesc() {
        this.sortType = 1;
        if (!this.isChildEmpty()) {
            // 执行排序
            List<ZKNodeTreeItem> childes = this.getChildren();
            childes.sort((a, b) -> b.nodePath().compareTo(a.nodePath()));
            childes.forEach(ZKNodeTreeItem::sortDesc);
        }
    }

    /**
     * 是否需要认证
     *
     * @return 结果
     */
    public boolean needAuth() {
        if (ZKAuthUtil.isNeedAuth(this.value, this.zkClient())) {
            return true;
        }
        return this.itemValue().graphic().getUrl().contains("lock");
    }

    // /**
    //  * 获取可见的节点
    //  *
    //  * @return 可见节点
    //  */
    // public ZKNodeTreeItem visiblyItem() {
    //     ZKNodeTreeItem item = this;
    //     do {
    //         if (item.visible) {
    //             return item;
    //         }
    //         item = item.parent();
    //     } while (item != null);
    //     return null;
    // }

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
        // this.parent().doFilter(this.treeView().itemFilter());
    }

    /**
     * 取消收藏节点
     */
    public void unCollect() {
        if (this.info().removeCollect(this.nodePath())) {
            ZKInfoStore.INSTANCE.update(this.info());
            // this.treeView().filterItem();
            // this.parent().doFilter(this.treeView().itemFilter());
        }
    }

    /**
     * zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.root().info();
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
        return this.zkClient().deleteACL(this.nodePath(), acl);
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

    @Override
    public ZKNodeTreeItemValue itemValue() {
        return (ZKNodeTreeItemValue) super.getValue();
    }

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

    public void flushStatus() {
        this.itemValue().flushStatus();
    }

    public SVGGlyph graphic() {
        return this.itemValue().graphic();
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
        this.zkClient().delQuota(this.nodePath(), true, true);
        this.zkClient().createQuota(this.nodePath(), bytes, num);
    }

    /**
     * 清除子节点数量配额
     *
     * @throws Exception 异常
     */
    public void clearQuotaNum() throws Exception {
        this.zkClient().delQuota(this.nodePath(), false, true);
    }

    /**
     * 清除节点数据配额
     *
     * @throws Exception 异常
     */
    public void clearQuotaBytes() throws Exception {
        this.zkClient().delQuota(this.nodePath(), true, false);
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
}
