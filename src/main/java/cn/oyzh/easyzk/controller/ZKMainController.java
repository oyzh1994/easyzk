package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKLeftCollapseEvent;
import cn.oyzh.easyzk.event.ZKLeftExtendEvent;
import cn.oyzh.easyzk.fx.ZKMsgTextArea;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.tabs.ZKTabPane;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.ResizeHelper;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
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
// @Lazy
// @Component
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
     * 左侧zk树
     */
    @FXML
    private ZKTreeView tree;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane tabPaneLeft;

    /**
     * 大小调整增强
     */
    private ResizeHelper resizeHelper;

    /**
     * 节点排序(正序)
     */
    @FXML
    private SVGGlyph sortAsc;

    /**
     * 节点排序(倒序)
     */
    @FXML
    private SVGGlyph sortDesc;

    /**
     * zk切换面板
     */
    @FXML
    private ZKTabPane tabPane;

    /**
     * 消息文本框
     */
    @FXML
    private ZKMsgTextArea msgArea;

    /**
     * zk历史Controller
     */
    @FXML
    private DataHistoryController dataHistoryController;

    /**
     * 对子节点排序，正序
     */
    @FXML
    private void sortAsc() {
        this.sortAsc.disappear();
        this.sortDesc.display();
        this.tree.sortAsc();
    }

    /**
     * 对子节点排序，倒序
     */
    @FXML
    private void sortDesc() {
        this.sortDesc.disappear();
        this.sortAsc.display();
        this.tree.sortDesc();
    }

    /**
     * 打开终端
     */
    @FXML
    private void openTerminal() {
        ZKEventUtil.terminalOpen();
    }

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
        EventUtil.register(this.tree);
        EventUtil.register(this.tabPane);
        EventUtil.register(this.msgArea);
        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeLeft(this.setting.getPageLeftWidth());
        }
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        EventUtil.unregister(this.tree);
        EventUtil.unregister(this.tabPane);
        EventUtil.unregister(this.msgArea);
        // 关闭连接
        this.tree.closeConnects();
        // 保存页面拉伸
        this.savePageResize();
        // 取消F5按键监听
        KeyListener.unListenReleased(this.tree, KeyCode.F5);
        KeyListener.unListenReleased(this.tabPane, KeyCode.F5);
    }

    /**
     * 左侧组件重新布局
     *
     * @param newWidth 新宽度
     */
    private void resizeLeft(Double newWidth) {
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
            this.setting.setPageLeftWidth(this.tabPaneLeft.getMinWidth());
            ZKSettingStore2.INSTANCE.replace(this.setting);
        }
    }

    @Override
    protected void bindListeners() {
        this.sortAsc.managedBindVisible();
        this.sortDesc.managedBindVisible();
        // zk树变化事件
        this.tree.selectItemChanged(this::treeItemChanged);

        // 文件拖拽初始化
        this.stage.initDragFile(this.tree.dragContent(), this.tree.getRoot()::dragFile);
        // 拖动改变redis树大小处理
        this.resizeHelper = new ResizeHelper(this.tabPaneLeft, Cursor.DEFAULT,this::resizeLeft);
        this.resizeHelper.widthLimit(240, 650);
        this.resizeHelper.initResizeEvent();
        // 搜索触发事件
        KeyListener.listenReleased(this.stage, new KeyHandler().keyCode(KeyCode.F).controlDown(true).handler(t1 -> ZKEventUtil.searchFire()));
        // 刷新触发事件
        KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
        // 刷新触发事件
        KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 树节点变化事件
     *
     * @param item 节点
     */
    private void treeItemChanged(TreeItem<?> item) {
        if (item instanceof ZKConnectTreeItem treeItem) {
            this.flushViewTitle(treeItem.value());
        } else {
            this.flushViewTitle(null);
        }
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    /**
     * 展开左侧
     */
    @EventSubscribe
    private void leftExtend(ZKLeftExtendEvent event) {
        this.tabPaneLeft.display();
        double w = this.tabPaneLeft.getMinWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.tabPaneLeft.parentAutosize();
    }

    /**
     * 收缩左侧
     */
    @EventSubscribe
    private void leftCollapse(ZKLeftCollapseEvent event) {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
    }

    @Override
    public List<SubStageController> getSubControllers() {
        List<SubStageController> list = new ArrayList<>();
        list.add(this.dataHistoryController);
        return list;
    }

    /**
     * 清空节点消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }
}
