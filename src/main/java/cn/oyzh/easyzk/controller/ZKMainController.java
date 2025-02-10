package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.controller.main.ConnectController;
import cn.oyzh.easyzk.controller.main.HistoryController;
import cn.oyzh.easyzk.controller.main.MessageController;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.tree.ZKTreeItemChangedEvent;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.tabs.ZKTabPane;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.connect.ZKDataTreeItem;
import cn.oyzh.easyzk.trees.connect.ZKQueriesTreeItem;
import cn.oyzh.easyzk.trees.connect.ZKTerminalTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.node.NodeResizeHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * zk主页
 *
 * @author oyzh
 * @since 2020/9/16
 */
public class ZKMainController extends ParentStageController {

    /**
     * 配置对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane tabPaneLeft;

    /**
     * zk切换面板
     */
    @FXML
    private ZKTabPane tabPane;

    /**
     * zk连接
     */
    @FXML
    private ConnectController connectController;

    /**
     * zk消息
     */
    @FXML
    private MessageController messageController;

    /**
     * zk历史
     */
    @FXML
    private HistoryController historyController;

    /**
     * 刷新窗口标题
     *
     * @param connect zk连接
     */
    private void flushViewTitle(ZKConnect connect) {
        if (connect != null) {
            this.stage.appendTitle(" (" + connect.getName() + ")");
        } else {
            this.stage.restoreTitle();
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeLeft(this.setting.getPageLeftWidth());
        }
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        // 保存页面拉伸
        this.savePageResize();
        // KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
    }

    /**
     * 左侧组件重新布局
     *
     * @param newWidth 新宽度
     */
    private void resizeLeft(Float newWidth) {
        if (newWidth != null && !Double.isNaN(newWidth)) {
            // 设置组件宽
            this.tabPaneLeft.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.tabPaneLeft.parentAutosize();
        }
    }

    @Override
    public void onSystemExit() {
        // 保存页面拉伸
        this.savePageResize();
    }

    /**
     * 保存页面拉伸
     */
    private void savePageResize() {
        if (this.setting.isRememberPageResize()) {
            this.setting.setPageLeftWidth((float) this.tabPaneLeft.getMinWidth());
            ZKSettingStore.INSTANCE.replace(this.setting);
        }
    }

    @Override
    protected void bindListeners() {
        // 大小调整增强
        NodeResizeHelper resizeHelper = new NodeResizeHelper(this.tabPaneLeft, Cursor.DEFAULT, this::resizeLeft);
        resizeHelper.widthLimit(240f, 650f);
        resizeHelper.initResizeEvent();
        // // 搜索触发事件
        // KeyListener.listenReleased(this.stage, new KeyHandler().keyCode(KeyCode.F).controlDown(true).handler(t1 -> ZKEventUtil.searchFire()));
        // // 刷新触发事件
        // KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 树节点变化事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void treeItemChanged(ZKTreeItemChangedEvent event) {
        if (event.data() instanceof ZKConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else if (event.data() instanceof ZKDataTreeItem treeItem) {
            this.flushViewTitle(treeItem.zkConnect());
        } else if (event.data() instanceof ZKQueriesTreeItem treeItem) {
            this.flushViewTitle(treeItem.zkConnect());
        } else if (event.data() instanceof ZKTerminalTreeItem treeItem) {
            this.flushViewTitle(treeItem.zkConnect());
        } else {
            this.flushViewTitle(null);
        }
    }

    /**
     * 布局2
     */
    @EventSubscribe
    private void layout2(Layout2Event event) {
        this.tabPaneLeft.display();
        double w = this.tabPaneLeft.realWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.tabPaneLeft.parentAutosize();
    }

    /**
     * 布局1
     */
    @EventSubscribe
    private void layout1(Layout1Event event) {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
    }

    @Override
    public List<SubStageController> getSubControllers() {
        return List.of(this.connectController, this.messageController, this.historyController);
    }
}
