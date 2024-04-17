package cn.oyzh.easyzk.trees.connect;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoTransportController;
import cn.oyzh.easyzk.controller.info.ZKInfoUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeExportController;
import cn.oyzh.easyzk.controller.node.ZKNodeImportController;
import cn.oyzh.easyzk.controller.node.ZKServiceController;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.trees.ZKConnectManager;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.CancelConnectMenuItem;
import cn.oyzh.fx.plus.menu.CloseConnectMenuItem;
import cn.oyzh.fx.plus.menu.CollapseAllMenuItem;
import cn.oyzh.fx.plus.menu.DeleteConnectMenuItem;
import cn.oyzh.fx.plus.menu.EditConnectMenuItem;
import cn.oyzh.fx.plus.menu.ExpandAllMenuItem;
import cn.oyzh.fx.plus.menu.ExportDataMenuItem;
import cn.oyzh.fx.plus.menu.ImportDataMenuItem;
import cn.oyzh.fx.plus.menu.LoadAllMenuItem;
import cn.oyzh.fx.plus.menu.OpenTerminalMenuItem;
import cn.oyzh.fx.plus.menu.RenameConnectMenuItem;
import cn.oyzh.fx.plus.menu.RepeatConnectMenuItem;
import cn.oyzh.fx.plus.menu.ServerInfoMenuItem;
import cn.oyzh.fx.plus.menu.StartConnectMenuItem;
import cn.oyzh.fx.plus.menu.TransportDataMenuItem;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * zk连接节点
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class ZKConnectTreeItem extends ZKTreeItem<ZKConnectTreeItemValue> {

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
    private ZKClient client;

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
        super(treeView);
        this.value(value);
        // 监听节点变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> ZKEventUtil.treeChildChanged());
    }

    /**
     * 连接状态属性
     *
     * @return 连接状态属性
     */
    public ReadOnlyObjectProperty<ZKConnState> stateProperty() {
        return this.client.stateProperty();
    }

    @Override
    public ZKNodeTreeItem firstChild() {
        return (ZKNodeTreeItem) super.firstChild();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isConnecting()) {
            CancelConnectMenuItem cancelConnect = new CancelConnectMenuItem("10", this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnected()) {
            CloseConnectMenuItem closeConnect = new CloseConnectMenuItem("10", this::closeConnect);
            EditConnectMenuItem editConnect = new EditConnectMenuItem("12", this::editConnect);
            RepeatConnectMenuItem repeatConnect = new RepeatConnectMenuItem("12", this::repeatConnect);
            ServerInfoMenuItem server = new ServerInfoMenuItem("12", this::serverInfo);
            ExportDataMenuItem exportData = new ExportDataMenuItem("12", () -> this.firstChild().exportNode());
            ImportDataMenuItem importData = new ImportDataMenuItem("12", this::importNode);
            TransportDataMenuItem transportData = new TransportDataMenuItem("12", this::transportData);
            server.setDisable(!this.client.initialized());

            items.add(closeConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(server);

            ZKNodeTreeItem firstChild = this.firstChild();
            // 根节点不为空，加载全部，收缩全部，展开全部菜单启用
            if (firstChild != null && firstChild.value().parentNode()) {
                LoadAllMenuItem loadAll = new LoadAllMenuItem("12", firstChild::loadChildAll);
                ExpandAllMenuItem expandAll = new ExpandAllMenuItem("12", firstChild::expandAll);
                CollapseAllMenuItem collapseAll = new CollapseAllMenuItem("12", firstChild::collapseAll);
                items.add(loadAll);
                items.add(expandAll);
                items.add(collapseAll);
            }
            OpenTerminalMenuItem openTerminal = new OpenTerminalMenuItem("12", this::openTerminal);
            items.add(openTerminal);
        } else {
            StartConnectMenuItem connect = new StartConnectMenuItem("12", this::connect);
            EditConnectMenuItem editConnect = new EditConnectMenuItem("12", this::editConnect);
            RenameConnectMenuItem renameConnect = new RenameConnectMenuItem("12", this::rename);
            DeleteConnectMenuItem deleteConnect = new DeleteConnectMenuItem("12", this::delete);
            RepeatConnectMenuItem repeatConnect = new RepeatConnectMenuItem("12", this::repeatConnect);
            ExportDataMenuItem exportData = new ExportDataMenuItem("12", this::exportNode);
            TransportDataMenuItem transportData = new TransportDataMenuItem("12", this::transportData);
            OpenTerminalMenuItem openTerminal = new OpenTerminalMenuItem("12", this::openTerminal);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(exportData);
            items.add(transportData);
            items.add(deleteConnect);
            items.add(openTerminal);
        }
        return items;
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

    /**
     * 查看服务信息
     */
    private void serverInfo() {
        StageWrapper fxView = StageUtil.parseStage(ZKServiceController.class, this.window());
        fxView.setProp("zkInfo", this.value);
        fxView.setProp("zkClient", this.client);
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
            this.client.close();
            this.stopWaiting();
        });
    }

    /**
     * 连接
     */
    public void connect() {
        if (!this.isConnected() && !this.isConnecting()) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(() -> {
                        this.client.startWithListener();
                        if (!this.isConnected()) {
                            if (!this.canceled) {
                                MessageBox.warn(this.value.getName() + "连接失败");
                            }
                            this.canceled = false;
                        } else {
                            this.loadRootNode();
                        }
                    })
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(MessageBox::exception)
                    .build();
            // 执行连接
            this.startWaiting(task);
        }
    }

    /**
     * 导入数据
     */
    private void importNode() {
        StageWrapper fxView = StageUtil.parseStage(ZKNodeImportController.class, this.window());
        fxView.setProp("zkClient", this.client);
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
     * 关闭连接
     */
    public void closeConnect() {
        if (this.isConnected()) {
            if (this.hasUnsavedNode() && !MessageBox.confirm("发现节点数据未保存，确定关闭连接？")) {
                return;
            }
            this.closeConnect(true);
        }
    }

    /**
     * 关闭连接
     *
     * @param waiting 是否开启等待动画
     */
    public void closeConnect(boolean waiting) {
        Runnable func = () -> {
            this.client.close();
            this.clearChild();
            this.flushGraphic();
        };
        if (waiting) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(func)
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        } else {
            func.run();
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
        if (!this.isConnected()) {
            this.connect();
        } else {
            super.free();
        }
    }

    /**
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnected()) {
            if (!MessageBox.confirm("需要关闭连接，继续么？")) {
                return;
            }
            this.closeConnect();
        }
        StageWrapper fxView = StageUtil.parseStage(ZKInfoUpdateController.class, this.window());
        fxView.setProp("zkInfo", this.value());
        fxView.display();
    }

    /**
     * 复制连接
     */
    private void repeatConnect() {
        ZKInfo zkInfo = new ZKInfo();
        zkInfo.copy(this.value);
        zkInfo.setName(this.value.getName() + "-复制");
        zkInfo.setCollects(Collections.emptyList());
        if (this.infoStore.add(zkInfo)) {
            this.parent().addConnect(zkInfo);
        } else {
            MessageBox.warn("复制连接失败！");
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm("删除" + this.value().getName(), "确定删除连接？")) {
            this.closeConnect(false);
            if (this.parent().delConnectItem(this)) {
                ZKEventUtil.infoDeleted(this.value);
            } else {
                MessageBox.warn("删除连接失败！");
            }
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
        this.value.setName(connectName);
        // 修改名称
        if (this.infoStore.update(this.value)) {
            this.setValue(new ZKConnectTreeItemValue(this));
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
        this.client = new ZKClient(value);
        this.client.stateProperty().addListener((observable, o, n) -> {
            // 连接关闭
            if (n == null || !n.isConnected()) {
                // 清理子节点
                this.clearChild();
            }
            // 连接中断事件
            if (n == ZKConnState.SUSPENDED) {
                this.client.close();
                MessageBox.warn(this.value().getName() + "连接中断");
            }
        });
        super.setValue(new ZKConnectTreeItemValue(this));
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.client != null && this.client.isConnecting();
    }

    /**
     * 加载ZK根节点
     */
    public void loadRootNode() {
        // 获取根节点
        ZKNode rootNode = ZKNodeUtil.getNode(this.client, "/");
        if (rootNode != null) {
            // 生成根节点
            ZKNodeTreeItem rootItem = new ZKNodeTreeItem(rootNode, this);
            // 设置根节点
            this.setChild(rootItem);
            // 展开连接
            this.extend();
            // 展开根节点
            rootItem.extend();
            // 加载全部节点
            if (this.setting.isLoadAll()) {
                rootItem.loadChildAll();
            } else if (!this.setting.isLoadRoot()) {// 加载一级节点
                rootItem.loadChild();
            }
            SystemUtil.gcLater();
        } else {
            MessageBox.warn(this.value().getName() + "加载根节点失败");
        }
    }

    /**
     * 获取全部zk子节点列表
     *
     * @return zk子节点列表
     */
    public List<ZKNodeTreeItem> getAllNodeItem() {
        List<ZKNodeTreeItem> list = new ArrayList<>();
        this.getAllNodeItem(this.firstChild(), list);
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
            for (TreeItem<?> treeItem : item.getRealChildren()) {
                if (treeItem instanceof ZKNodeTreeItem nodeTreeItem) {
                    this.getAllNodeItem(nodeTreeItem, list);
                }
            }
        }
    }

    /**
     * 寻找zk节点
     *
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull String targetPath) {
        if (this.isConnected() && !this.isChildEmpty()) {
            return this.findNodeItem(this.firstChild(), targetPath);
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
        // 遍历子节点，寻找匹配节点
        for (ZKNodeTreeItem item : root.showChildren()) {
            ZKNodeTreeItem treeItem = this.findNodeItem(item, targetPath);
            // 返回节点信息
            if (treeItem != null) {
                return treeItem;
            }
        }
        return null;
    }

    @Override
    public void sortAsc() {
        if (super.isSortable()) {
            ZKNodeTreeItem firstChild = this.firstChild();
            if (firstChild != null) {
                firstChild.sortAsc();
            }
        }
    }

    @Override
    public void sortDesc() {
        if (super.isSortable()) {
            ZKNodeTreeItem firstChild = this.firstChild();
            if (firstChild != null) {
                firstChild.sortDesc();
            }
        }
    }

    /**
     * 获取当前父节点
     *
     * @return 父节点
     */
    public ZKConnectManager parent() {
        Object object = this.getParent();
        if (object instanceof ZKConnectManager connectManager) {
            return connectManager;
        }
        return null;
    }

    @Override
    public boolean allowDrag() {
        return true;
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.connect();
    }
}
