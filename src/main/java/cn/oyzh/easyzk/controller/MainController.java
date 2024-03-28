package cn.oyzh.easyzk.controller;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKPageInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKPageInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * 主页
 *
 * @author oyzh
 * @since 2022/8/19
 */
@StageAttribute(
        usePrimary = true,
        title = "EasyZK主页",
        iconUrls = ZKConst.ICON_PATH,
        // cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "main.fxml"
)
public class MainController extends ParentController {

    /**
     * 项目信息
     */
    private final Project project = new Project();

    /**
     * 头部页面
     */
    @FXML
    private HeaderController headerController;

    /**
     * zk主页业务
     */
    @FXML
    private ZKMainController zkMainController;

    /**
     * 页面信息
     */
    private final ZKPageInfo pageInfo = ZKPageInfoStore.PAGE_INFO;

    /**
     * zk相关配置
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 页面信息储存
     */
    private final ZKPageInfoStore pageInfoStore = ZKPageInfoStore.INSTANCE;

    /**
     * 初始化系统托盘
     */
    private void initSystemTray() {
        if (!TrayManager.exist()) {
            try {
                // 初始化
                TrayManager.init(ZKConst.ICON_PATH);
                // 设置标题
                TrayManager.setTitle(this.project.getName() + " v" + this.project.getVersion());
                // 打开主页
                TrayManager.addMenuItem("打开", new SVGGlyph("/font/desktop.svg", "12"), this::showMain);
                // 打开设置
                TrayManager.addMenuItem("设置", new SVGGlyph("/font/setting.svg", "12"), this::showSetting);
                // 退出程序
                TrayManager.addMenuItem("退出", new SVGGlyph("/font/poweroff.svg", "12"), () -> {
                    StaticLog.warn("exit app by tray.");
                    // this.exit();
                    StageUtil.exit();
                });
                // 鼠标事件
                TrayManager.onMouseClicked(e -> {
                    // 单击鼠标主键，显示主页
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        this.showMain();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 显示设置
     */
    private void showSetting() {
        FXUtil.runLater(() -> {
            StageWrapper wrapper = StageUtil.getStage(SettingController.class);
            if (wrapper != null) {
                StaticLog.info("front setting.");
                wrapper.toFront();
            } else {
                StaticLog.info("show setting.");
                StageUtil.showStage(SettingController.class, this.stage);
            }
        });
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            StageWrapper wrapper = StageUtil.getStage(MainController.class);
            if (wrapper != null) {
                StaticLog.info("front main.");
                wrapper.toFront();
            } else {
                StaticLog.info("show main.");
                StageUtil.showStage(MainController.class);
            }
        });
    }

    @Override
    public List<? extends Controller> getSubControllers() {
        return Arrays.asList(this.zkMainController, this.headerController);
    }

    @Override
    public void onStageCloseRequest(WindowEvent event) {
        StaticLog.warn("main view closing.");
        // 直接退出应用
        if (this.setting.isExitDirectly()) {
            StaticLog.info("exit directly.");
            // this.exit();
            StageUtil.exit();
        } else if (this.setting.isExitAsk()) { // 总是询问
            if (MessageBox.confirm("确定退出" + this.project.getName() + "？")) {
                StaticLog.info("exit by confirm.");
                // this.exit();
                StageUtil.exit();
            } else {
                StaticLog.info("cancel by confirm.");
                event.consume();
            }
        } else if (this.setting.isExitTray()) {// 系统托盘
            if (TrayManager.exist()) {
                StaticLog.info("show tray.");
                TrayManager.show();
            } else {
                StaticLog.error("tray not support!");
                MessageBox.warn("不支持系统托盘！");
            }
        }
    }

    @Override
    public void onStageShowing(WindowEvent event) {
        super.onStageShowing(event);
        this.stage.setTitleExt(this.project.getName() + "-v" + this.project.getVersion());
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        super.onStageHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 注册事件处理
        EventUtil.register(this);
        try {
            this.initSystemTray();
            TrayManager.show();
        } catch (Exception ex) {
            StaticLog.warn("不支持系统托盘!");
            ex.printStackTrace();
        }
    }

    // /**
    //  * 应用退出
    //  */
    // @EventReceiver(ZKEventTypes.APP_EXIT)
    // public void exit() {
    //     StageUtil.exit();
    // }

    @Override
    public void onSystemExit() {
        boolean savePageInfo = false;
        // 记住页面大小
        if (this.setting.isRememberPageSize()) {
            this.pageInfo.setWidth(this.stage.getWidth());
            this.pageInfo.setHeight(this.stage.getHeight());
            this.pageInfo.setMaximized(this.stage.isMaximized());
            savePageInfo = true;
        }
        // 记住页面位置
        if (this.setting.isRememberPageLocation()) {
            this.pageInfo.setScreenX(this.stage.getX());
            this.pageInfo.setScreenY(this.stage.getY());
            savePageInfo = true;
        }
        // 保存页面信息
        if (savePageInfo) {
            this.pageInfoStore.update(this.pageInfo);
        }
        // 关闭托盘
        TrayManager.destroy();
        super.onSystemExit();
    }

    @Override
    public void onStageInitialize(StageWrapper stage) {
        super.onStageInitialize(stage);
        // 设置上次保存的页面大小
        if (this.setting.isRememberPageSize()) {
            if (this.pageInfo.isMaximized()) {
                this.stage.setMaximized(true);
                StaticLog.debug("view setMaximized");
            } else if (this.pageInfo.getWidth() != null && this.pageInfo.getHeight() != null) {
                this.stage.setWidth(this.pageInfo.getWidth());
                this.stage.setHeight(this.pageInfo.getHeight());
                StaticLog.debug("view setWidth:{} setHeight:{}", this.pageInfo.getWidth(), this.pageInfo.getHeight());
            }
        }
        // 设置上次保存的页面位置
        if (this.setting.isRememberPageLocation() && !this.pageInfo.isMaximized() && this.pageInfo.getScreenX() != null && this.pageInfo.getScreenY() != null) {
            this.stage.setX(this.pageInfo.getScreenX());
            this.stage.setY(this.pageInfo.getScreenY());
            StaticLog.debug("view setX:{} setY:{}", this.pageInfo.getScreenX(), this.pageInfo.getScreenY());
        }
    }
}
