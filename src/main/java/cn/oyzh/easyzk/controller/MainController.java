package cn.oyzh.easyzk.controller;

import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.plus.controller.FXController;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.tray.FXSystemTray;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.view.FXView;
import cn.oyzh.fx.plus.view.FXViewUtil;
import cn.oyzh.fx.plus.view.FXWindow;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.PageInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.store.PageInfoStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
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
@FXWindow(
        usePrimary = true,
        title = "EasyZK主页",
        iconUrls = ZKConst.ICON_PATH,
        cssUrls = ZKStyle.MAIN,
        value = ZKConst.FXML_BASE_PATH + "main.fxml"
)
public class MainController extends FXController {

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
            FXView fxView = FXViewUtil.getView(SettingController.class);
            if (fxView != null) {
                log.info("front setting.");
                fxView.toFront();
            } else {
                log.info("show setting.");
                FXViewUtil.showView(SettingController.class, this.view);
            }
        });
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            FXView fxView = FXViewUtil.getView(MainController.class);
            if (fxView != null) {
                log.info("front main.");
                fxView.toFront();
            } else {
                log.info("show main.");
                FXViewUtil.showView(MainController.class);
            }
        });
    }

    @Override
    public List<FXController> getSubControllers() {
        return Arrays.asList(this.zkMainController, this.headerController);
    }

    @Override
    public void onViewCloseRequest(WindowEvent event) {
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
    public void onViewShowing(WindowEvent event) {
        super.onViewShowing(event);
        this.view.setTitle(this.project.getName() + "-v" + this.project.getVersion());
    }

    @Override
    public void onViewHidden(WindowEvent event) {
        super.onViewHidden(event);
        // 取消注册事件处理
        EventUtil.unregister(this);
    }

    @Override
    public void onViewShown(WindowEvent event) {
        super.onViewShown(event);
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
        FXViewUtil.exit();
    }

    @Override
    public void onSystemExit() {
        boolean savePageInfo = false;
        // 记住页面大小
        if (this.setting.isRememberPageSize()) {
            this.pageInfo.setWidth(this.view.getWidth());
            this.pageInfo.setHeight(this.view.getHeight());
            this.pageInfo.setMaximized(this.view.isMaximized());
            savePageInfo = true;
        }
        // 记住页面位置
        if (this.setting.isRememberPageLocation()) {
            this.pageInfo.setScreenX(this.view.getX());
            this.pageInfo.setScreenY(this.view.getY());
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
    public void onViewInitialize(FXView view) {
        super.onViewInitialize(view);
        // 设置上次保存的页面大小
        if (this.setting.isRememberPageSize()) {
            if (this.pageInfo.isMaximized()) {
                this.view.setMaximized(true);
                if (log.isDebugEnabled()) {
                    log.debug("view setMaximized");
                }
            } else if (this.pageInfo.getWidth() != null && this.pageInfo.getHeight() != null) {
                this.view.setWidth(this.pageInfo.getWidth());
                this.view.setHeight(this.pageInfo.getHeight());
                if (log.isDebugEnabled()) {
                    log.debug("view setWidth:{} setHeight:{}", this.pageInfo.getWidth(), this.pageInfo.getHeight());
                }
            }
        }
        // 设置上次保存的页面位置
        if (this.setting.isRememberPageLocation() && !this.pageInfo.isMaximized() && this.pageInfo.getScreenX() != null && this.pageInfo.getScreenY() != null) {
            this.view.setX(this.pageInfo.getScreenX());
            this.view.setY(this.pageInfo.getScreenY());
            if (log.isDebugEnabled()) {
                log.debug("view setX:{} setY:{}", this.pageInfo.getScreenX(), this.pageInfo.getScreenY());
            }
        }
    }
}
