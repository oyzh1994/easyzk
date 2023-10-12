package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.PageInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.store.PageInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controller.ParentController;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.tray.FXSystemTray;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

/**
 * 主页
 *
 * @author oyzh
 * @since 2022/8/19
 */
@Slf4j
@StageAttribute(
        usePrimary = true,
        title = "EasyZK主页",
        iconUrls = ZKConst.ICON_PATH,
        cssUrls = ZKStyle.MAIN,
        value = ZKConst.FXML_BASE_PATH + "main.fxml"
)
public class MainController extends ParentController {

    /**
     * 项目信息
     */
    @Autowired
    private Project project;

    /**
     * 系统托盘
     */
    private static FXSystemTray tray;

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
    private final PageInfo pageInfo = PageInfoStore.PAGE_INFO;

    /**
     * zk相关配置
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 页面信息储存
     */
    private final PageInfoStore pageInfoStore = PageInfoStore.INSTANCE;

    /**
     * 初始化系统托盘
     */
    private void initSystemTray() {
        if (tray != null) {
            return;
        }
        try {
            // 初始化托盘
            tray = new FXSystemTray(ZKConst.ICON_PATH);
            // 设置标题
            tray.setTitle(this.project.getName() + " v" + this.project.getVersion());
            // 打开主页
            tray.addMenuItem("打开", new SVGGlyph("/font/desktop.svg", "12"), this::showMain);
            // 打开设置
            tray.addMenuItem("设置", new SVGGlyph("/font/setting.svg", "12"), this::showSetting);
            // 退出程序
            tray.addMenuItem("退出", new SVGGlyph("/font/poweroff.svg", "12"), () -> {
                log.warn("exit app by tray.");
                this.exit();
            });
            // 鼠标事件
            tray.onMouseClicked(e -> {
                // 单击鼠标主键，显示主页
                if (e.getButton() == MouseEvent.BUTTON1) {
                    this.showMain();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 显示设置
     */
    private void showSetting() {
        FXUtil.runLater(() -> {
            StageWrapper wrapper = StageUtil.getStage(SettingController.class);
            if (wrapper != null) {
                log.info("front setting.");
                wrapper.toFront();
            } else {
                log.info("show setting.");
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
                log.info("front main.");
                wrapper.toFront();
            } else {
                log.info("show main.");
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
        log.warn("main view closing.");
        // 直接退出应用
        if (this.setting.isExitDirectly()) {
            log.info("exit directly.");
            this.exit();
            return;
        }

        // 总是询问
        if (this.setting.isExitAsk()) {
            if (FXAlertUtil.confirm("确定退出" + this.project.getName() + "？")) {
                log.info("exit by confirm.");
                this.exit();
            } else {
                log.info("cancel by confirm.");
                event.consume();
            }
            return;
        }

        // 系统托盘
        if (this.setting.isExitTray()) {
            if (tray != null) {
                log.info("show tray.");
                tray.show();
            } else {
                log.error("tray not support!");
                FXAlertUtil.warn("不支持系统托盘！");
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
            tray.show();
        } catch (Exception ex) {
            log.warn("不支持系统托盘!");
            ex.printStackTrace();
        }
    }

    /**
     * 应用退出
     */
    @EventReceiver(ZKEventTypes.APP_EXIT)
    public void exit() {
        StageUtil.exit();
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
        if (tray != null) {
            tray.close();
        }
        super.onSystemExit();
    }

    @Override
    public void onStageInitialize(StageWrapper stage) {
        super.onStageInitialize(stage);
        // 设置上次保存的页面大小
        if (this.setting.isRememberPageSize()) {
            if (this.pageInfo.isMaximized()) {
                this.stage.setMaximized(true);
                if (log.isDebugEnabled()) {
                    log.debug("view setMaximized");
                }
            } else if (this.pageInfo.getWidth() != null && this.pageInfo.getHeight() != null) {
                this.stage.setWidth(this.pageInfo.getWidth());
                this.stage.setHeight(this.pageInfo.getHeight());
                if (log.isDebugEnabled()) {
                    log.debug("view setWidth:{} setHeight:{}", this.pageInfo.getWidth(), this.pageInfo.getHeight());
                }
            }
        }
        // 设置上次保存的页面位置
        if (this.setting.isRememberPageLocation() && !this.pageInfo.isMaximized() && this.pageInfo.getScreenX() != null && this.pageInfo.getScreenY() != null) {
            this.stage.setX(this.pageInfo.getScreenX());
            this.stage.setY(this.pageInfo.getScreenY());
            if (log.isDebugEnabled()) {
                log.debug("view setX:{} setY:{}", this.pageInfo.getScreenX(), this.pageInfo.getScreenY());
            }
        }
    }
}
