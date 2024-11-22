package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.controller.main.ConnectController;
import cn.oyzh.easyzk.controller.main.HistoryController;
import cn.oyzh.easyzk.controller.main.MessageController;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKTreeItemChangedEvent;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.tabs.ZKTabPane;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.NodeResizeHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
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
    private final ZKSetting setting = ZKSettingStore2.SETTING;

    /**
     * 当前激活的zk信息
     */
    private ZKInfo info;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane tabPaneLeft;

    /**
     * 大小调整增强
     */
    private NodeResizeHelper resizeHelper;

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
     * @param info zk信息
     */
    private void flushViewTitle(ZKInfo info) {
        if (info != null) {
            this.stage.appendTitle(" (" + info.getName() + ")");
        } else {
            this.stage.restoreTitle();
        }
        this.info = info;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
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
        KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
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
            ZKSettingStore2.INSTANCE.replace(this.setting);
        }
    }

    @Override
    protected void bindListeners() {
        // 拖动改变redis树大小处理
        this.resizeHelper = new NodeResizeHelper(this.tabPaneLeft, Cursor.DEFAULT,this::resizeLeft);
        this.resizeHelper.widthLimit(240f, 650f);
        this.resizeHelper.initResizeEvent();
        // // 搜索触发事件
        // KeyListener.listenReleased(this.stage, new KeyHandler().keyCode(KeyCode.F).controlDown(true).handler(t1 -> ZKEventUtil.searchFire()));
        // 刷新触发事件
        KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
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
        double w = this.tabPaneLeft.getMinWidth();
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
        List<SubStageController> list = new ArrayList<>();
        list.add(this.connectController);
        list.add(this.messageController);
        list.add(this.historyController);
        return list;
    }
}
