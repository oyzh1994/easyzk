package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.domain.PageInfo;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.msg.ZKInfoUpdatedMsg;
import cn.oyzh.easyzk.msg.ZKMsg;
import cn.oyzh.easyzk.msg.ZKMsgFormat;
import cn.oyzh.easyzk.msg.ZKSearchFinishMsg;
import cn.oyzh.easyzk.store.PageInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.tabs.ZKTabPane;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.trees.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.fx.common.Const;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventGroup;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.node.ResizeEnhance;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * zk节点主页
 *
 * @author oyzh
 * @since 2020/9/16
 */
@Lazy
@Slf4j
@Component
public class ZKMainController extends ParentController {

    /**
     * 配置对象
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 当前激活的zk信息
     */
    private ZKInfo zkInfo;

    /**
     * 左侧zk树
     */
    @FXML
    public ZKTreeView tree;

    /**
     * 左侧组件
     */
    @FXML
    private FlexTabPane zkMainLeft;

    /**
     * 大小调整增强
     */
    private ResizeEnhance resizeEnhance;

    // /**
    //  * 倒序排序
    //  */
    // private boolean ascSort;

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
     * 仅看临时节点
     */
    @FXML
    private FlexCheckBox onlyCollect;

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
     * zk切换面板
     */
    @FXML
    public ZKTabPane tabPane;

    /**
     * 消息文本框
     */
    @FXML
    private MsgTextArea msgArea;

    /**
     * zk搜索Controller
     */
    @FXML
    private SearchController searchController;

    /**
     * 页面信息
     */
    private final PageInfo pageInfo = PageInfoStore.PAGE_INFO;

    /**
     * 页面信息储存
     */
    private final PageInfoStore pageInfoStore = PageInfoStore.INSTANCE;

    /**
     * 对子节点排序，正序
     */
    @FXML
    private void sortAsc() {
        this.sortDesc.disappear();
        this.sortAsc.display();
        this.tree.sortAsc();
    }

    /**
     * 对子节点排序，倒序
     */
    @FXML
    private void sortDesc() {
        this.sortAsc.disappear();
        this.sortDesc.display();
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
        TaskManager.startDelayTask("zk:tree:filter", () -> {
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
     * @param msg 消息
     */
    @EventReceiver(value = ZKEventTypes.ZK_INFO_UPDATED, async = true)
    private void onZKInfoUpdate(ZKInfoUpdatedMsg msg) {
        if (this.zkInfo == msg.info()) {
            this.flushViewTitle(msg.info());
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
        this.zkInfo = info;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this);
        EventUtil.register(this.tree);
        EventUtil.register(this.tabPane);
        this.filter();

        // 设置上次保存的页面拉伸
        if (this.setting.isRememberPageResize()) {
            this.resizeMainLeft(this.pageInfo.getMainLeftWidth());
        }
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
        EventUtil.unregister(this.tree);
        EventUtil.unregister(this.tabPane);
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
            this.zkMainLeft.setRealWidth(newWidth);
            this.tabPane.setLayoutX(newWidth);
            this.tabPane.setFlexWidth("100% - " + newWidth);
            this.zkMainLeft.parentAutosize();
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
            this.pageInfo.setMainLeftWidth(this.zkMainLeft.getMinWidth());
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
        this.sortAsc.managedProperty().bind(this.sortAsc.visibleProperty());
        this.sortDesc.managedProperty().bind(this.sortDesc.visibleProperty());
        this.tabPane.selectedTabChanged((abs, o, n) -> {
            if (o != null) {
                o.getStyleClass().remove("tab-active");
            }
            if (n != null) {
                n.getStyleClass().add("tab-active");
            }
        });

        // zk树选中节点变化事件
        this.tree.selectItemChanged(item -> {
            if (item instanceof ZKNodeTreeItem treeItem) {
                this.tabPane.initNodeTab(treeItem);
                this.flushViewTitle(treeItem.info());
            } else if (item instanceof ZKConnectTreeItem treeItem) {
                this.flushViewTitle(treeItem.info());
            } else {
                this.flushViewTitle(null);
            }
        });

        // 文件拖拽初始化
        this.stage.initDragFile(this.tree.dragContent(), this.tree.root()::dragFile);
        // 拖动改变zk树大小处理
        this.resizeEnhance = new ResizeEnhance(this.zkMainLeft, Cursor.DEFAULT);
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
        // 监听F5按键
        KeyListener.listenReleased(this.tree, KeyCode.F5, keyEvent -> this.tree.reload());
        KeyListener.listenReleased(this.tabPane, KeyCode.F5, keyEvent -> this.tabPane.reload());
    }

    /**
     * 定位节点
     */
    @FXML
    private void positionNode() {
        this.tree.scrollTo(this.tree.getSelectedItem());
    }

    /**
     * 树节点过滤
     */
    @EventReceiver(value = ZKEventTypes.TREE_CHILD_FILTER, async = true, verbose = true)
    private void onTreeChildFilter() {
        this.tree.itemFilter().initFilters();
        this.filter();
    }

    /**
     * 搜索开始事件
     */
    @EventReceiver(value = ZKEventTypes.ZK_SEARCH_START, verbose = true)
    private void searchStart() {
        this.tree.itemFilter().setSearchParam(null);
        this.filter();
    }

    /**
     * 搜索结束事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = ZKEventTypes.ZK_SEARCH_FINISH, verbose = true)
    private void searchEnd(ZKSearchFinishMsg msg) {
        this.tree.itemFilter().setSearchParam(msg.searchParam());
        this.filter();
    }

    /**
     * 展开左侧
     */
    @EventReceiver(value = ZKEventTypes.LEFT_EXTEND, async = true, verbose = true)
    private void leftExtend() {
        this.zkMainLeft.display();
        double w = this.zkMainLeft.getMinWidth();
        this.tabPane.setLayoutX(w);
        this.tabPane.setFlexWidth("100% - " + w);
        this.zkMainLeft.parentAutosize();
        log.info("LEFT_EXTEND.");
    }

    /**
     * 收缩左侧
     */
    @EventReceiver(value = ZKEventTypes.LEFT_COLLAPSE, async = true, verbose = true)
    private void leftCollapse() {
        this.zkMainLeft.disappear();
        this.tabPane.setLayoutX(0);
        this.tabPane.setFlexWidth("100%");
        this.zkMainLeft.parentAutosize();
        log.info("LEFT_COLLAPSE.");
    }

    @Override
    public List<SubController> getSubControllers() {
        return Collections.singletonList(this.searchController);
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

    /**
     * 处理zk消息
     */
    @EventGroup(value = ZKEventGroups.NODE_ACTION, async = true, verbose = true)
    @EventGroup(value = ZKEventGroups.INFO_ACTION, async = true, verbose = true)
    @EventGroup(value = ZKEventGroups.CONNECTION_ACTION, async = true, verbose = true)
    private void onZKMsg(Event<ZKMsg> event) {
        if (event.data() instanceof ZKMsgFormat format) {
            String formatMsg = format.formatMsg();
            if (formatMsg != null) {
                this.msgArea.appendLine(String.format("%s %s", Const.DATE_TIME_FORMAT.format(System.currentTimeMillis()), formatMsg));
            }
        }
    }
}
