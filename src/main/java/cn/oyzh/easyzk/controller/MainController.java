package cn.oyzh.easyzk.controller;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

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
        iconUrls = ZKConst.ICON_PATH,
        value = ZKConst.FXML_BASE_PATH + "main.fxml"
)
public class MainController extends ParentStageController {

    /**
     * 项目信息
     */
//    @Resource
    private final Project project = Project.load();

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

    // /**
    //  * 页面信息
    //  */
    // private final ZKPageInfo pageInfo = ZKPageInfoStore.PAGE_INFO;

    /**
     * zk相关配置
     */
    private final ZKSetting setting = ZKSettingStore2.SETTING;

    // /**
    //  * 页面信息储存
    //  */
    // private final ZKPageInfoStore pageInfoStore = ZKPageInfoStore.INSTANCE;

    // /**
    //  * 初始化系统托盘
    //  */
    // private void initSystemTray() {
    //     if (!TrayManager.supported()) {
    //         JulLog.warn("tray is not supported.");
    //         return;
    //     }
    //     if (!TrayManager.exist()) {
    //         try {
    //             // 初始化
    //             TrayManager.init(ZKConst.ICON_PATH);
    //             // 设置标题
    //             TrayManager.setTitle(this.project.getName() + " v" + this.project.getVersion());
    //             // 打开主页
    //             TrayManager.addMenuItem(new DesktopTrayItem("12", this::showMain));
    //             // 打开设置
    //             TrayManager.addMenuItem(new SettingTrayItem("12", this::showSetting));
    //             // 退出程序
    //             TrayManager.addMenuItem(new QuitTrayItem("12", () -> {
    //                 JulLog.warn("exit app by tray.");
    //                 StageManager.exit();
    //             }));
    //             // 鼠标事件
    //             TrayManager.onMouseClicked(e -> {
    //                 // 单击鼠标主键，显示主页
    //                 if (e.getButton() == MouseEvent.BUTTON1) {
    //                     this.showMain();
    //                 }
    //             });
    //         } catch (Exception ex) {
    //             ex.printStackTrace();
    //         }
    //     }
    // }

    // /**
    //  * 显示设置
    //  */
    // private void showSetting() {
    //     FXUtil.runLater(() -> {
    //         StageAdapter wrapper = StageManager.getStage(SettingController.class);
    //         if (wrapper != null) {
    //             JulLog.info("front setting.");
    //             wrapper.toFront();
    //         } else {
    //             JulLog.info("show setting.");
    //             StageManager.showStage(SettingController.class, this.stage);
    //         }
    //     });
    // }
    //
    // /**
    //  * 显示主页
    //  */
    // private void showMain() {
    //     FXUtil.runLater(() -> {
    //         StageAdapter wrapper = StageManager.getStage(MainController.class);
    //         if (wrapper != null) {
    //             JulLog.info("front main.");
    //             wrapper.toFront();
    //         } else {
    //             JulLog.info("show main.");
    //             StageManager.showStage(MainController.class);
    //         }
    //     });
    // }

    @Override
    public List<? extends StageController> getSubControllers() {
        return Arrays.asList(this.zkMainController, this.headerController);
    }

    @Override
    public void onStageCloseRequest(WindowEvent event) {
        JulLog.warn("main view closing.");
        // 直接退出应用
        if (this.setting.isExitDirectly()) {
            JulLog.info("exit directly.");
            StageManager.exit();
        } else if (this.setting.isExitAsk()) { // 总是询问
            if (MessageBox.confirm(I18nHelper.quit() + " " + this.project.getName())) {
                JulLog.info("exit by confirm.");
                StageManager.exit();
            } else {
                JulLog.info("cancel by confirm.");
                event.consume();
            }
        } else if (this.setting.isExitTray()) {// 系统托盘
            if (TrayManager.exist()) {
                JulLog.info("show tray.");
                event.consume();
                // 仅隐藏到任务栏
                this.stage.setIconified(true);
                // 显示托盘
                TrayManager.show();
            } else {
                JulLog.error("tray not support!");
                MessageBox.warn(I18nHelper.trayNotSupport());
            }
        }
    }

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        this.stage.setTitleExt(this.project.getName() + "-v" + this.project.getVersion());
    }

    // @Override
    // public void onStageShown(WindowEvent event) {
    //     super.onStageShown(event);
    //     try {
    //         // this.initSystemTray();
    //         TrayManager.show();
    //     } catch (Exception ex) {
    //         JulLog.warn("不支持系统托盘!");
    //         ex.printStackTrace();
    //     }
    // }

    @Override
    public void onSystemExit() {
        boolean savePageInfo = false;
        // 记住页面大小
        if (this.setting.isRememberPageSize()) {
            this.setting.setPageWidth(this.stage.getWidth());
            this.setting.setPageHeight(this.stage.getHeight());
            this.setting.setPageMaximized(this.stage.isMaximized());
            savePageInfo = true;
        }
        // 记住页面位置
        if (this.setting.isRememberPageLocation()) {
            this.setting.setPageScreenX(this.stage.getX());
            this.setting.setPageScreenY(this.stage.getY());
            savePageInfo = true;
        }
        // 保存页面信息
        if (savePageInfo) {
            ZKSettingStore2.INSTANCE.replace(this.setting);
        }
        super.onSystemExit();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        super.onStageInitialize(stage);
        // 设置上次保存的页面大小
        if (this.setting.isRememberPageSize()) {
            if (this.setting.isPageMaximized()) {
                this.stage.setMaximized(true);
                JulLog.debug("view maximized");
            } else if (this.setting.getPageWidth() != null && this.setting.getPageHeight() != null) {
                this.stage.setSize(this.setting.getPageWidth(), this.setting.getPageHeight());
                JulLog.debug("view width:{} height:{}", this.setting.getPageWidth(), this.setting.getPageHeight());
            }
        }
        // 设置上次保存的页面位置
        if (this.setting.isRememberPageLocation() && !this.setting.isPageMaximized() && this.setting.getPageScreenX() != null && this.setting.getPageScreenY() != null) {
            this.stage.setLocation(this.setting.getPageScreenX(), this.setting.getPageScreenY());
            JulLog.debug("view x:{} y:{}", this.setting.getPageScreenX(), this.setting.getPageScreenY());
        }
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.main");
    }
}
