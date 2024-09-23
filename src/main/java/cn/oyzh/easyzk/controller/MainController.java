package cn.oyzh.easyzk.controller;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKPageInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKPageInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.tray.DesktopTrayItem;
import cn.oyzh.fx.plus.tray.QuitTrayItem;
import cn.oyzh.fx.plus.tray.SettingTrayItem;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

import javax.annotation.Resource;
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
        iconUrls = ZKConst.ICON_PATH,
        value = ZKConst.FXML_BASE_PATH + "main.fxml"
)
public class MainController extends ParentStageController {

    /**
     * 项目信息
     */
    @Resource
    private Project project;

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
    private final ZKSetting setting = ZKSettingStore2.SETTING;

    /**
     * 页面信息储存
     */
    private final ZKPageInfoStore pageInfoStore = ZKPageInfoStore.INSTANCE;

    /**
     * 初始化系统托盘
     */
    private void initSystemTray() {
        if (!TrayManager.supported()) {
            StaticLog.warn("tray is not supported.");
            return;
        }
        if (!TrayManager.exist()) {
            try {
                // 初始化
                TrayManager.init(ZKConst.ICON_PATH);
                // 设置标题
                TrayManager.setTitle(this.project.getName() + " v" + this.project.getVersion());
                // 打开主页
                TrayManager.addMenuItem(new DesktopTrayItem("12", this::showMain));
                // 打开设置
                TrayManager.addMenuItem(new SettingTrayItem("12", this::showSetting));
                // 退出程序
                TrayManager.addMenuItem(new QuitTrayItem("12", () -> {
                    StaticLog.warn("exit app by tray.");
                    StageManager.exit();
                }));
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
            StageAdapter wrapper = StageManager.getStage(SettingController.class);
            if (wrapper != null) {
                StaticLog.info("front setting.");
                wrapper.toFront();
            } else {
                StaticLog.info("show setting.");
                StageManager.showStage(SettingController.class, this.stage);
            }
        });
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(MainController.class);
            if (wrapper != null) {
                StaticLog.info("front main.");
                wrapper.toFront();
            } else {
                StaticLog.info("show main.");
                StageManager.showStage(MainController.class);
            }
        });
    }

    @Override
    public List<? extends StageController> getSubControllers() {
        return Arrays.asList(this.zkMainController, this.headerController);
    }

    @Override
    public void onStageCloseRequest(WindowEvent event) {
        StaticLog.warn("main view closing.");
        // 直接退出应用
        if (this.setting.isExitDirectly()) {
            StaticLog.info("exit directly.");
            StageManager.exit();
        } else if (this.setting.isExitAsk()) { // 总是询问
            if (MessageBox.confirm(I18nHelper.quit() + this.project.getName())) {
                StaticLog.info("exit by confirm.");
                StageManager.exit();
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
                MessageBox.warn(I18nHelper.trayNotSupport());
            }
        }
    }

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        this.stage.setTitleExt(this.project.getName() + "-v" + this.project.getVersion());
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        try {
            this.initSystemTray();
            TrayManager.show();
        } catch (Exception ex) {
            StaticLog.warn("不支持系统托盘!");
            ex.printStackTrace();
        }
    }

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
    public void onStageInitialize(StageAdapter stage) {
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

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.main");
    }
}
