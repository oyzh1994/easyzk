package cn.oyzh.easyzk;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyzk.controller.MainController;
import cn.oyzh.easyzk.controller.SettingController2;
import cn.oyzh.easyzk.controller.data.ZKMigrationTipsController;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.easyzk.terminal.ZKTerminalManager;
import cn.oyzh.easyzk.zk.ZKSASLUtil;
import cn.oyzh.event.EventFactory;
import cn.oyzh.fx.gui.tray.DesktopTrayItem;
import cn.oyzh.fx.gui.tray.QuitTrayItem;
import cn.oyzh.fx.gui.tray.SettingTrayItem;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.event.FxEventBus;
import cn.oyzh.fx.plus.event.FxEventConfig;
import cn.oyzh.fx.plus.ext.FXApplication;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.terminal.util.TerminalManager;
import cn.oyzh.i18n.I18nManager;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2020/9/14
 */
public class EasyZKApp extends FXApplication {

    /**
     * 项目信息
     */
    private static final Project PROJECT = Project.load();

    public static void main(String[] args) {
        try {
            // 抗锯齿优化
            System.setProperty("prism.text", "t2k");
            System.setProperty("prism.lcdtext", "false");
            SysConst.projectName(PROJECT.getName());
            JulLog.info("项目启动中...");
            // 储存初始化
            ZKStoreUtil.init();
            // 注册sasl处理器
            ZKSASLUtil.registerConfiguration();
            SysConst.storeDir(ZKConst.STORE_PATH);
            SysConst.cacheDir(ZKConst.CACHE_PATH);
            FXConst.appIcon(ZKConst.ICON_PATH);
            EventFactory.registerEventBus(FxEventBus.class);
            EventFactory.syncEventConfig(FxEventConfig.SYNC);
            EventFactory.asyncEventConfig(FxEventConfig.ASYNC);
            EventFactory.defaultEventConfig(FxEventConfig.DEFAULT);
            // TerminalConst.scanBase("cn.oyzh.easyzk.terminal");
            // 初始化时区处理器
            // System.setProperty("java.time.zone.DefaultZoneRulesProvider", LocalZoneRulesProvider.class.getName());
            launch(EasyZKApp.class, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("main error", ex);
        }
    }

    @Override
    public void init() throws Exception {
        try {
            // fx程序实例
            FXConst.INSTANCE = this;
            // 日志开始
            JulLog.info("{} init start.", SysConst.projectName());
            // 禁用fx的css日志
            FXUtil.disableCSSLogger();
            // 配置对象
            ZKSetting setting = ZKSettingStore.SETTING;
            // 应用区域
            I18nManager.apply(setting.getLocale());
            // 应用字体
            FontManager.apply(setting.fontConfig());
            // 应用主题
            ThemeManager.apply(setting.themeConfig());
            // 应用透明度
            OpacityManager.apply(setting.opacityConfig());
            // 注册异常处理器
            MessageBox.registerExceptionParser(ZKExceptionParser.INSTANCE);
            // 调用父类
            super.init();
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("main error", ex);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            super.start(primaryStage);
            // 注册命令
            TerminalManager.setLoadHandlerAction(ZKTerminalManager::registerHandlers);
            // 显示迁移弹窗
            if (ZKStoreUtil.checkOlder()) {
                FXUtil.runWait(() -> StageManager.showStage(ZKMigrationTipsController.class, primaryStage), 1000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("start error", ex);
        }
    }

    @Override
    protected void showMainView() {
        try {
            // 显示主页面
            StageManager.showStage(MainController.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("showMainView error", ex);
        }
    }

    @Override
    protected void initSystemTray() {
        try {
            if (!TrayManager.supported()) {
                JulLog.warn("tray is not supported.");
                return;
            }
            if (TrayManager.exist()) {
                return;
            }
            // 初始化
            TrayManager.init(ZKConst.TRAY_ICON_PATH);
            // 设置标题
            TrayManager.setTitle(PROJECT.getName() + " v" + PROJECT.getVersion());
            // 打开主页
            TrayManager.addMenuItem(new DesktopTrayItem("12", this::showMain));
            // 打开设置
            TrayManager.addMenuItem(new SettingTrayItem("12", this::showSetting));
            // 退出程序
            TrayManager.addMenuItem(new QuitTrayItem("12", () -> {
                JulLog.warn("exit app by tray.");
                StageManager.exit();
            }));
            // 鼠标事件
            TrayManager.onMouseClicked(e -> {
                // 单击鼠标主键，显示主页
                if (e.getButton() == MouseEvent.BUTTON1) {
                    this.showMain();
                }
            });
            // 显示托盘
            TrayManager.show();
        } catch (Exception ex) {
            JulLog.warn("不支持系统托盘!", ex);
        }
    }

    /**
     * 显示主页
     */
    private void showMain() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(MainController.class);
            if (wrapper != null) {
                JulLog.info("front main.");
                wrapper.toFront();
            } else {
                JulLog.info("show main.");
                StageManager.showStage(MainController.class);
            }
        });
    }

    /**
     * 显示设置
     */
    private void showSetting() {
        FXUtil.runLater(() -> {
            StageAdapter wrapper = StageManager.getStage(SettingController2.class);
            if (wrapper != null) {
                JulLog.info("front setting.");
                wrapper.toFront();
            } else {
                JulLog.info("show setting.");
                StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
            }
        });
    }
}
