package cn.oyzh.easyzk.fx;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoTransportController;
import cn.oyzh.easyzk.controller.info.ZKInfoUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeImportController;
import cn.oyzh.easyzk.controller.node.ZKServiceController;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * zk连接节点
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class ZKConnectTreeItem extends BaseTreeItem {

    /**
     * zk信息
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKInfo value;

    /**
     * zk客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKClient zkClient;

    /**
     * 已取消操作标志位
     */
    private boolean canceled;

    /**
     * 配置储存对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * zk信息储存
     */
    private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    public ZKConnectTreeItem(@NonNull ZKInfo value, @NonNull ZKTreeView treeView) {
        this.treeView(treeView);
        this.value(value);
        // 监听节点变化
        this.getChildren().addListener((ListChangeListener<? super ZKNodeTreeItem>) c -> {
            ZKEventUtil.treeChildChanged();
            this.treeView().flushLocal();
        });
    }

    /**
     * 连接状态属性
     *
     * @return 连接状态属性
     */
    public ReadOnlyObjectProperty<ZKConnState> stateProperty() {
        return this.zkClient.stateProperty();
    }

    /**
     * 获取根节点
     *
     * @return zk节点
     */
    public ZKNodeTreeItem root() {
        if (this.isChildEmpty()) {
            return null;
        }
        return this.getChildren().get(0);
    }

    /**
     * 设置根节点
     *
     * @param root zk节点
     */
    public void root(@NonNull ZKNodeTreeItem root) {
        if (this.isChildEmpty()) {
            this.getChildren().add(root);
        } else {
            this.getChildren().set(0, root);
        }
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isConnecting()) {
            MenuItem cancelConnect = MenuItemExt.newItem("取消连接", new SVGGlyph("/font/close.svg", "11"), "取消zk连接", this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnect()) {
            MenuItemExt closeConnect = MenuItemExt.newItem("关闭连接", new SVGGlyph("/font/poweroff.svg", "12"), "关闭zk连接(快捷键pause)", this::closeConnect);
            MenuItemExt editConnect = MenuItemExt.newItem("编辑连接", new SVGGlyph("/font/edit.svg", "12"), "编辑zk连接", this::editConnect);
            MenuItemExt server = MenuItemExt.newItem("服务信息", new SVGGlyph("/font/sever.svg", "12"), "查看连接服务信息", this::serverInfo);
            MenuItemExt exportData = MenuItemExt.newItem("导出数据", new SVGGlyph("/font/export.svg", "12"), "导出zk数据", () -> this.root().exportNode());
            MenuItemExt importData = MenuItemExt.newItem("导入数据", new SVGGlyph("/font/Import.svg", "12"), "导入zk数据", this::importNode);
            MenuItemExt transportData = MenuItemExt.newItem("传输数据", new SVGGlyph("/font/arrow-left-right-line.svg", "12"), "传输zk数据到其他连接", this::transportData);
            server.setDisable(!this.zkClient.initialized());

            items.add(closeConnect);
            items.add(editConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(server);

            // 根节点不为空，加载全部，收缩全部，展开全部菜单启用
            if (this.root() != null && this.root().value().parentNode()) {
                MenuItemExt expandAll = MenuItemExt.newItem("展开全部", new SVGGlyph("/font/colum-height.svg", "12"), "展开全部zk子节点", this.root()::expandAll);
                MenuItemExt loadAll = MenuItemExt.newItem("加载全部", new SVGGlyph("/font/reload time.svg", "12"), "加载全部zk子节点", this.root()::loadChildAll);
                MenuItemExt collapseAll = MenuItemExt.newItem("收缩全部", new SVGGlyph("/font/vertical-align-middl.svg", "12"), "收缩全部zk子节点", this.root()::collapseAll);
                items.add(loadAll);
                items.add(expandAll);
                items.add(collapseAll);
            }
            MenuItemExt openTerminal = MenuItemExt.newItem("打开终端", new SVGGlyph("/font/code library.svg", "12"), "打开终端窗口", this::openTerminal);
            items.add(openTerminal);
        } else {
            MenuItemExt connect = MenuItemExt.newItem("开始连接", new SVGGlyph("/font/play-circle.svg", "12"), "开始连接zk(鼠标左键双击)", this::connect);
            MenuItemExt editConnect = MenuItemExt.newItem("编辑连接", new SVGGlyph("/font/edit.svg", "12"), "编辑zk连接", this::editConnect);
            MenuItemExt renameConnect = MenuItemExt.newItem("连接更名", new SVGGlyph("/font/edit-square.svg", "12"), "更改连接名称(快捷键f2)", this::rename);
            MenuItemExt deleteConnect = MenuItemExt.newItem("删除连接", new SVGGlyph("/font/delete.svg", "12"), "删除zk连接(快捷键delete)", this::delete);
            MenuItemExt transportData = MenuItemExt.newItem("传输数据", new SVGGlyph("/font/arrow-left-right-line.svg", "12"), "传输zk数据到其他连接", this::transportData);
            MenuItemExt openTerminal = MenuItemExt.newItem("打开终端", new SVGGlyph("/font/code library.svg", "12"), "打开终端窗口", this::openTerminal);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(transportData);
            items.add(deleteConnect);
            items.add(openTerminal);
        }
        return items;
    }

    /**
     * 查看服务信息
     */
    private void serverInfo() {
        StageWrapper fxView = StageUtil.parseStage(ZKServiceController.class, this.treeView().window());
        fxView.setProp("zkInfo", this.info());
        fxView.setProp("zkClient", this.zkClient);
        fxView.display();
    }

    /**
     * 打开终端
     */
    private void openTerminal() {
        ZKEventUtil.terminalOpen(this.value);
    }

    /**
     * 取消连接
     */
    public void cancelConnect() {
        this.canceled = true;
        ThreadUtil.startVirtual(() -> {
            this.zkClient.close();
            this.stopWaiting();
        });
    }

    /**
     * 连接
     */
    public void connect() {
        if (!this.isConnect() && !this.isConnecting()) {
            // 执行连接
            this.startWaiting();
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.zkClient.startWithListener();
                        if (!this.zkClient.isConnected()) {
                            if (!this.canceled) {
                                MessageBox.warn(info().getName() + "连接失败");
                            }
                            this.canceled = false;
                        } else {
                            this.loadRootNode();
                        }
                    })
                    .onFinish(this::stopWaiting)
                    .build();
            ThreadUtil.startVirtual(task);
        }
    }

    /**
     * 导入数据
     */
    private void importNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKNodeImportController.class, this.treeView().window());
        fxView.setProp("zkClient", this.zkClient);
        fxView.display();
    }

    /**
     * 传输数据
     */
    @FXML
    private void transportData() {
        StageWrapper wrapper = StageUtil.getStage(ZKInfoTransportController.class);
        if (wrapper != null) {
            wrapper.disappear();
        }
        wrapper = StageUtil.parseStage(ZKInfoTransportController.class);
        wrapper.setProp("formConnect", this.value);
        wrapper.display();
    }

    /**
     * zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.zkClient == null ? null : this.zkClient.zkInfo();
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (!this.isWaiting() && this.isConnect()) {
            if (this.hasUnsavedNode() && !MessageBox.confirm("发现节点数据未保存，确定关闭连接？")) {
                return;
            }
            this.startWaiting();
            Task task = TaskBuilder.newBuilder()
                    .onStart(this.zkClient::closeManual)
                    .onSuccess(() -> {
                        this.clearChildren();
                        SystemUtil.gcLater();
                    }).onFinish(this::stopWaiting)
                    .onError(MessageBox::exception)
                    .build();
            ThreadUtil.startVirtual(task);
        }
    }

    /**
     * 是否有未保存数据的节点
     *
     * @return 结果
     */
    private boolean hasUnsavedNode() {
        List<ZKNodeTreeItem> items = this.getAllNodeItem();
        for (ZKNodeTreeItem item : items) {
            if (item.dataUnsaved()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void free() {
        if (!this.isConnect()) {
            this.connect();
        } else {
            super.free();
        }
    }

    /**
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnect() && MessageBox.confirm("需要关闭连接，继续么？")) {
            this.closeConnect();
        }
        StageWrapper fxView = StageUtil.parseStage(ZKInfoUpdateController.class, this.treeView().window());
        fxView.setProp("zkInfo", this.value());
        fxView.display();
    }

    @Override
    public void delete() {
        if (MessageBox.confirm("删除" + this.info().getName(), "确定删除连接？")) {
            this.closeConnect();
            if (this.getParent() instanceof ConnectManager connectManager) {
                if (!connectManager.delConnectItem(this)) {
                    MessageBox.warn("删除连接失败！");
                }
            }
            ZKEventUtil.infoDeleted(this.value);
        }
    }

    @Override
    public void rename() {
        String connectName = MessageBox.prompt("请输入新的连接名称", this.value.getName());

        // 名称为null或者跟当前名称相同，则忽略
        if (connectName == null || Objects.equals(connectName, this.value.getName())) {
            return;
        }

        // 检查名称
        if (StrUtil.isBlank(connectName)) {
            MessageBox.warn("连接名称不能为空！");
            return;
        }

        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(connectName);
        if (this.infoStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn("此连接名称已存在！");
            return;
        }

        // 修改名称
        if (this.infoStore.update(this.value)) {
            this.itemValue(new ZKConnectTreeItemValue(this));
        } else {
            MessageBox.warn("修改连接名称失败！");
        }
    }

    /**
     * 设置值
     *
     * @param value zk信息
     */
    public void value(@NonNull ZKInfo value) {
        this.value = value;
        this.zkClient = new ZKClient(value);
        this.zkClient.stateProperty().addListener((observable, o, n) -> {
            // 连接关闭
            if (n == null || !n.isConnected()) {
                // 清理子节点
                this.clearChildren();
            }
            // 连接中断事件
            if (n == ZKConnState.SUSPENDED) {
                this.zkClient.close();
                MessageBox.warn(this.info().getName() + "连接中断");
            }
        });
        super.setValue(new ZKConnectTreeItemValue(this));
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnect() {
        return this.zkClient != null && this.zkClient.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.zkClient != null && this.zkClient.isConnecting();
    }

    /**
     * 加载ZK根节点
     */
    public void loadRootNode() {
        // 获取根节点
        ZKNode rootNode = ZKNodeUtil.getNode(this.zkClient, "/");
        if (rootNode != null) {
            // 生成根节点
            ZKNodeTreeItem rootItem = new ZKNodeTreeItem(rootNode, this);
            // 设置根节点
            this.root(rootItem);
            // 展开连接
            this.extend();
            // 展开根节点
            this.root().extend();
            // 加载全部节点
            if (this.setting.isLoadAll()) {
                rootItem.loadChildes(true);
            } else if (!this.setting.isLoadRoot()) {// 加载一级节点
                rootItem.loadChildes(false);
            }
            SystemUtil.gcLater();
        } else {
            MessageBox.warn(this.info().getName() + "加载根节点失败");
        }
    }

    @Override
    public ObservableList<ZKNodeTreeItem> getChildren() {
        return super.getChildren();
    }

    /**
     * 清理子节点
     */
    public void clearChildren() {
        try {
            this.setExpanded(false);
            this.getChildren().clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取全部zk子节点列表
     *
     * @return zk子节点列表
     */
    public List<ZKNodeTreeItem> getAllNodeItem() {
        List<ZKNodeTreeItem> list = new ArrayList<>();
        this.getAllNodeItem(this.root(), list);
        return list;
    }

    /**
     * 获取全部zk子节点列表
     *
     * @param item zk节点
     * @param list zk子节点列表
     */
    private void getAllNodeItem(ZKNodeTreeItem item, List<ZKNodeTreeItem> list) {
        list.add(item);
        for (ZKNodeTreeItem treeItem : item.children()) {
            this.getAllNodeItem(treeItem, list);
        }
    }

    /**
     * 寻找zk节点
     *
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull String targetPath) {
        if (this.isConnect() && !this.isChildEmpty()) {
            return this.findNodeItem(this.root(), targetPath);
        }
        return null;
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
        List<ZKNodeTreeItem> children = root.children();
        // 遍历子节点，寻找匹配节点
        for (ZKNodeTreeItem item : children) {
            ZKNodeTreeItem treeItem = this.findNodeItem(item, targetPath);
            // 返回节点信息
            if (treeItem != null) {
                return treeItem;
            }
        }
        return null;
    }

    @Override
    public void sort(Boolean sortOrder) {
        if (sortOrder != null && this.isConnect()) {
            this.root().sort(sortOrder);
        }
    }

    @Override
    public void filter(@NonNull ZKTreeItemFilter filter) {
        if (this.isConnect() && this.root() != null) {
            this.root().filter(filter);
            this.root().flushChildren();
        }
    }

    /**
     * 获分组节点
     *
     * @return 分组节点
     */
    public ZKGroupTreeItem getGroupItem() {
        if (this.getParent() instanceof ZKGroupTreeItem groupItem) {
            return groupItem;
        }
        return null;
    }

    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public ZKConnectTreeItemValue itemValue() {
        return (ZKConnectTreeItemValue) super.itemValue();
    }
}
