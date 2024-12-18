package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.common.thread.Task;
import cn.oyzh.common.thread.TaskBuilder;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.controller.data.ZKDataExportController;
import cn.oyzh.easyzk.controller.data.ZKDataImportController;
import cn.oyzh.easyzk.controller.data.ZKDataTransportController;
import cn.oyzh.easyzk.controller.info.ZKInfoUpdateController;
import cn.oyzh.easyzk.controller.node.ZKServiceController;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.fx.ZookeeperSVGGlyph;
import cn.oyzh.easyzk.store.ZKConnectJdbcStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
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
public class ZKConnectTreeItem extends RichTreeItem<ZKConnectTreeItem.ZKConnectTreeItemValue> {

    /**
     * zk信息
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKConnect value;

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

    public ZKConnectTreeItem(@NonNull ZKConnect value, @NonNull RichTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        this.value(value);
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        if (this.isConnecting()) {
            FXMenuItem cancelConnect = MenuItemHelper.cancelConnect("12", this::cancelConnect);
            items.add(cancelConnect);
        } else if (this.isConnected()) {
            FXMenuItem closeConnect = MenuItemHelper.closeConnect("12", this::closeConnect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);
            FXMenuItem server = MenuItemHelper.serverInfo("12", this::serverInfo);
            FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem importData = MenuItemHelper.importData("12", this::importData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
            server.setDisable(!this.client.initialized());

            items.add(closeConnect);
            items.add(editConnect);
            items.add(repeatConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(server);
        } else {
            FXMenuItem connect = MenuItemHelper.startConnect("12", this::connect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem renameConnect = MenuItemHelper.renameConnect("12", this::rename);
            FXMenuItem deleteConnect = MenuItemHelper.deleteConnect("12", this::delete);
            FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem importData = MenuItemHelper.importData("12", this::importData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(exportData);
            items.add(importData);
            items.add(transportData);
            items.add(repeatConnect);
            items.add(deleteConnect);
        }
        return items;
    }

    /**
     * 导出zk节点
     */
    public void exportData() {
        // StageAdapter fxView = StageManager.parseStage(ZKNodeExportController.class, this.window());
        // fxView.setProp("zkItem", this);
        // fxView.setProp("zkClient", this.client());
        StageAdapter fxView = StageManager.parseStage(ZKDataExportController.class, this.window());
        fxView.setProp("connect", this.value);
        fxView.setProp("nodePath", "/");
        fxView.display();
    }

    /**
     * 查看服务信息
     */
    private void serverInfo() {
        StageAdapter fxView = StageManager.parseStage(ZKServiceController.class, this.window());
        fxView.setProp("zkInfo", this.value);
        fxView.setProp("zkClient", this.client);
        fxView.display();
    }

    /**
     * 取消连接
     */
    public void cancelConnect() {
        this.canceled = true;
        ThreadUtil.startVirtual(() -> this.client.close());
    }

    /**
     * 连接
     */
    public void connect() {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }
        Task task = TaskBuilder.newBuilder()
                .onStart(() -> {
                    this.client.startWithListener();
                    if (!this.isConnected()) {
                        if (!this.canceled) {
                            MessageBox.warn("[" + this.value.getName() + "] " + I18nHelper.connectFail());
                        }
                        this.canceled = false;
                        this.closeConnect(false);
                    } else {
                        ZKDataTreeItem dataItem = new ZKDataTreeItem(this.getTreeView());
                        ZKQueryTreeItem queryItem = new ZKQueryTreeItem(this.getTreeView());
                        ZKTerminalTreeItem terminalItem = new ZKTerminalTreeItem(this.getTreeView());
                        this.setChild(List.of(dataItem, queryItem, terminalItem));
                        this.expend();
                    }
                })
                .onSuccess(this::flushLocal)
                .onError(MessageBox::exception)
                .build();
        // 执行连接
        this.startWaiting(task);
    }

    /**
     * 导入数据
     */
    private void importData() {
        StageAdapter fxView = StageManager.parseStage(ZKDataImportController.class);
        fxView.setProp("connect", this.value);
        fxView.display();
    }

    /**
     * 传输数据
     */
    private void transportData() {
        // StageAdapter adapter = StageManager.getStage(ZKDataTransportController.class);
        // if (adapter != null) {
        //     adapter.disappear();
        // }
        StageAdapter adapter = StageManager.parseStage(ZKDataTransportController.class);
        adapter.setProp("sourceInfo", this.value);
        adapter.display();
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (this.isConnected()) {
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
        };
        if (waiting) {
            Task task = TaskBuilder.newBuilder()
                    .onStart(func::run)
                    .onSuccess(this::refresh)
                    .onError(MessageBox::exception)
                    .build();
            this.startWaiting(task);
        } else {
            func.run();
        }
    }

    /**
     * 编辑连接
     */
    private void editConnect() {
        if (this.isConnected()) {
            if (!MessageBox.confirm(I18nHelper.closeAndContinue())) {
                return;
            }
            this.closeConnect();
        }
        StageAdapter fxView = StageManager.parseStage(ZKInfoUpdateController.class, this.window());
        fxView.setProp("zkInfo", this.value());
        fxView.display();
    }

    /**
     * 复制连接
     */
    private void repeatConnect() {
        ZKConnect zkInfo = new ZKConnect();
        zkInfo.copy(this.value);
        zkInfo.setName(this.value.getName() + "-" + I18nHelper.repeat());
        zkInfo.setCollects(Collections.emptyList());
        if (ZKConnectJdbcStore.INSTANCE.replace(zkInfo)) {
            this.connectManager().addConnect(zkInfo);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.value().getName() + "]")) {
            this.closeConnect(false);
            if (this.connectManager().delConnectItem(this)) {
                ZKEventUtil.infoDeleted(this.value);
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    @Override
    public void rename() {
        String connectName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (connectName == null || Objects.equals(connectName, this.value.getName())) {
            return;
        }
        // 检查名称
        if (StringUtil.isBlank(connectName)) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
            return;
        }
        this.value.setName(connectName);
        // 修改名称
        if (ZKConnectJdbcStore.INSTANCE.replace(this.value)) {
            this.setValue(new ZKConnectTreeItemValue(this));
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    /**
     * 设置值
     *
     * @param value zk信息
     */
    public void value(@NonNull ZKConnect value) {
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
                MessageBox.warn(this.value().getName() + I18nHelper.connectSuspended());
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
     * 获取当前父节点
     *
     * @return 父节点
     */
    public ZKConnectManager connectManager() {
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
        if (!this.isConnected() && !this.isConnecting()) {
            this.connect();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    public String infoName() {
        return this.value.getName();
    }

    public String getId() {
        return this.value.getId();
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    @Accessors(chain = true, fluent = true)
    public static class ZKConnectTreeItemValue extends RichTreeItemValue {

        public ZKConnectTreeItemValue(@NonNull ZKConnectTreeItem item) {
            super(item);
        }

        @Override
        protected ZKConnectTreeItem item() {
            return (ZKConnectTreeItem) super.item();
        }

        @Override
        public String name() {
            return this.item().value().getName();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new ZookeeperSVGGlyph(12);
            }
            return super.graphic();
        }

        @Override
        public Color graphicColor() {
            if (this.item().isConnected() || this.item().isConnecting()) {
                return Color.GREEN;
            }
            return super.graphicColor();
        }
    }
}
