package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.controller.info.ZKInfoTransportController;
import cn.oyzh.easyzk.controller.info.ZKInfoUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeExportController;
import cn.oyzh.easyzk.controller.node.ZKNodeImportController;
import cn.oyzh.easyzk.controller.node.ZKServiceController;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKInfoStore2;
import cn.oyzh.easyzk.store.ZKSettingStore2;
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
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
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
    private final ZKSetting setting = ZKSettingStore2.SETTING;

    // /**
    //  * zk信息储存
    //  */
    // private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    public ZKConnectTreeItem(@NonNull ZKInfo value, @NonNull ZKTreeView treeView) {
        super(treeView);
        this.value(value);
        // 监听变化
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

            ZKNodeTreeItem firstChild = this.firstChild();
            // 根节点不为空，加载全部，收缩全部，展开全部菜单启用
            if (firstChild != null && firstChild.value().parentNode()) {
                FXMenuItem loadAll = MenuItemHelper.loadAll("12", this::loadChildAll);
                FXMenuItem expandAll = MenuItemHelper.expandAll("12", this::expandAll);
                FXMenuItem collapseAll = MenuItemHelper.collapseAll("12", this::collapseAll);
                items.add(loadAll);
                items.add(expandAll);
                items.add(collapseAll);
            }
        } else {
            FXMenuItem connect = MenuItemHelper.startConnect("12", this::connect);
            FXMenuItem editConnect = MenuItemHelper.editConnect("12", this::editConnect);
            FXMenuItem renameConnect = MenuItemHelper.renameConnect("12", this::rename);
            FXMenuItem deleteConnect = MenuItemHelper.deleteConnect("12", this::delete);
            FXMenuItem repeatConnect = MenuItemHelper.repeatConnect("12", this::repeatConnect);
            FXMenuItem exportData = MenuItemHelper.exportData("12", this::exportData);
            FXMenuItem transportData = MenuItemHelper.transportData("12", this::transportData);

            items.add(connect);
            items.add(editConnect);
            items.add(renameConnect);
            items.add(repeatConnect);
            items.add(exportData);
            items.add(transportData);
            items.add(deleteConnect);
        }
        FXMenuItem openTerminal = MenuItemHelper.openTerminal("12", this::openTerminal);
        items.add(openTerminal);
        return items;
    }

    private void collapseAll() {
        ZKNodeTreeItem firstChild = this.firstChild();
        if (firstChild != null) {
            firstChild.collapseAll();
        }
    }

    private void expandAll() {
        ZKNodeTreeItem firstChild = this.firstChild();
        if (firstChild != null) {
            firstChild.expandAll();
        }
    }

    private void loadChildAll() {
        ZKNodeTreeItem firstChild = this.firstChild();
        if (firstChild != null) {
            firstChild.loadChildAll();
        }
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
                                MessageBox.warn("[" + this.value.getName() + "] " + I18nHelper.connectFail());
                            }
                            this.canceled = false;
                            this.closeConnect(false);
                        } else {
                            // this.loadRootNode();
                            ZKEventUtil.connectionOpened(this);
                        }
                        this.flushGraphic();
                    })
                    .onFinish(this::stopWaiting)
                    .onSuccess(this::flushLocal)
                    .onError(MessageBox::exception)
                    .build();
            // 执行连接
            this.startWaiting(task);
        } else {
            ZKEventUtil.connectionOpened(this);
        }
    }

    /**
     * 导入数据
     */
    private void importData() {
        StageAdapter fxView = StageManager.parseStage(ZKNodeImportController.class, this.window());
        fxView.setProp("zkClient", this.client);
        fxView.display();
    }

    /**
     * 传输数据
     */
    private void transportData() {
        StageAdapter wrapper = StageManager.getStage(ZKInfoTransportController.class);
        if (wrapper != null) {
            wrapper.disappear();
        }
        wrapper = StageManager.parseStage(ZKInfoTransportController.class);
        wrapper.setProp("formConnect", this.value);
        wrapper.display();
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (this.isConnected()) {
            if (this.hasUnsavedNode() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
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
                    .onFinish(() -> {
                        this.stopWaiting();
                        this.flushGraphic();
                    })
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
        ZKInfo zkInfo = new ZKInfo();
        zkInfo.copy(this.value);
        zkInfo.setName(this.value.getName() + "-" + I18nHelper.repeat());
        zkInfo.setCollects(Collections.emptyList());
        if (ZKInfoStore2.INSTANCE.replace(zkInfo)) {
            this.parent().addConnect(zkInfo);
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (MessageBox.confirm(I18nHelper.delete() + " [" + this.value().getName() + "]")) {
            this.closeConnect(false);
            if (this.parent().delConnectItem(this)) {
                ZKEventUtil.infoDeleted(this.value);
//                // 删除历史记录
//                ZKDataHistoryStore2.INSTANCE.delete(this.value.getId());
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
        if (ZKInfoStore2.INSTANCE.replace(this.value)) {
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
                MessageBox.warn(this.value().getName() + I18nResourceBundle.i18nString("base.connectSuspended"));
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
        } else {
            MessageBox.warn(this.value().getName() + I18nHelper.loadFail());
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

    public String infoName() {
        return this.value.getName();
    }

    /**
     * 获取图标
     *
     * @return 图标
     */
    public SVGGlyph graphic() {
        return this.getValue().graphic();
    }
}
