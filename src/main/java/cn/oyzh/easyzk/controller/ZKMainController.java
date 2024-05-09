package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKPageInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKInfoUpdatedEvent;
import cn.oyzh.easyzk.event.ZKLeftCollapseEvent;
import cn.oyzh.easyzk.event.ZKLeftExtendEvent;
import cn.oyzh.easyzk.fx.ZKMsgTextArea;
import cn.oyzh.easyzk.store.ZKPageInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.tabs.ZKTabPane;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.ResizeEnhance;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


/**
 * zk主页
 *
 * @author oyzh
 * @since 2020/9/16
 */
@Lazy
@Component
public class ZKMainController extends ParentController {

    /**
     * 配置对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 当前激活的zk信息
     */
    private ZKInfo info;

    /**
     * 左侧zk树
     */
    @FXML
    public ZKTreeView tree;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane tabPaneLeft;

    /**
     * 大小调整增强
     */
    private ResizeEnhance resizeEnhance;

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
     * 仅看收藏
     */
    @FXML
    private FlexCheckBox onlyCollect;

    /**
     * zk切换面板
     */
    @FXML
    public ZKTabPane tabPane;

    /**
     * 消息文本框
     */
    @FXML
    private ZKMsgTextArea msgArea;

    /**
     * 过滤子节点
     */
    @FXML
    private FlexCheckBox filterSubNode;

    /**
     * 过滤临时节点
     */
    @FXML
    private FlexCheckBox filterEphemeral;

    /**
     * zk历史Controller
     */
    @FXML
    private DataHistoryController dataHistoryController;

    /**
     * 搜索Controller
     */
    @FXML
    private SearchController searchController;

    /**
     * 页面信息
     */
    private final ZKPageInfo pageInfo = ZKPageInfoStore.PAGE_INFO;

    /**
     * 页面信息储存
     */
    private final ZKPageInfoStore pageInfoStore = ZKPageInfoStore.INSTANCE;

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
     * 执行过滤
     */
    private void filter() {
        TaskManager.startDelay("zk:tree:filter", () -> {
            this.tree.disable();
            if (this.onlyCollect.isSelected()) {
                this.tree.itemFilter().setOnlyCollect(true);
                this.tree.itemFilter().setExcludeSub(false);
                this.tree.itemFilter().setExcludeEphemeral(false);
            } else {
                this.tree.itemFilter().setOnlyCollect(false);
                this.tree.itemFilter().setExcludeSub(this.filterSubNode.isSelected());
                this.tree.itemFilter().setExcludeEphemeral(this.filterEphemeral.isSelected());
            }
            this.tree.filter();
            this.tree.enable();
        }, 100);
    }

    /**
     * zk信息修改事件
     *
     * @param event 消息
     */
    private void infoUpdate(ZKInfoUpdatedEvent event) {
        if (this.info == event.data()) {
            this.flushViewTitle(event.data());
        }
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
        this.filter();

        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
        }
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
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
    private void resizeMainLeft(Double newWidth) {
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
            this.pageInfo.setMainLeftWidth(this.tabPaneLeft.getMinWidth());
            this.pageInfoStore.update(this.pageInfo);
        }
    }

    @Override
    protected void bindListeners() {
        // 左侧栏业务
        this.onlyCollect.selectedChanged((obs, o, n) -> {
            if (n) {
                this.filterSubNode.disable();
                this.filterEphemeral.disable();
            } else {
                this.filterSubNode.enable();
                this.filterEphemeral.enable();
            }
            this.filter();
        });
        this.filterSubNode.selectedChanged((obs, o, n) -> this.filter());
        this.filterEphemeral.selectedChanged((obs, o, n) -> this.filter());
        this.sortAsc.managedBindVisible();
        this.sortDesc.managedBindVisible();
        // zk树变化事件
        this.tree.selectItemChanged(this::treeItemChanged);

        // 文件拖拽初始化
        this.stage.initDragFile(this.tree.dragContent(), this.tree.root()::dragFile);
        // 拖动改变redis树大小处理
        this.resizeEnhance = new ResizeEnhance(this.tabPaneLeft, Cursor.DEFAULT);
        this.resizeEnhance.minWidth(390d);
        this.resizeEnhance.maxWidth(800d);
        this.resizeEnhance.triggerThreshold(8d);
        this.resizeEnhance.mouseDragged(event -> {
            double sceneX = event.getSceneX();
            if (this.resizeEnhance.resizeWidthAble(sceneX)) {
                // 左侧组件重新布局
                this.resizeMainLeft(sceneX);
            }
        });
        // 初始化拉伸事件
        this.tree.setOnMouseMoved(this.resizeEnhance.mouseMoved());
        this.resizeEnhance.initResizeEvent();

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
        if (item instanceof ZKNodeTreeItem treeItem) {
            this.flushViewTitle(treeItem.info());
            ZKEventUtil.treeChildSelected(treeItem);
        } else if (item instanceof ZKConnectTreeItem treeItem) {
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
    @Subscribe
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
    @Subscribe
    private void leftCollapse(ZKLeftCollapseEvent event) {
        this.tabPaneLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.tabPaneLeft.parentAutosize();
    }

    @Override
    public List<SubController> getSubControllers() {
        List<SubController> list = new ArrayList<>();
        list.add(this.searchController);
        list.add(this.dataHistoryController);
        return list;
    }

    /**
     * 当前活跃的zk树节点
     *
     * @return zk树节点
     */
    public ZKNodeTreeItem activeItem() {
        if (this.tabPane.getSelectedItem() instanceof ZKNodeTab itemTab) {
            return itemTab.treeItem();
        }
        return null;
    }

    /**
     * 清空节点消息
     */
    @FXML
    private void clearMsg() {
        this.msgArea.clear();
    }
}
