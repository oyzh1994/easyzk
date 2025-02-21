package cn.oyzh.easyzk;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.system.OSUtil;
import cn.oyzh.easyzk.controller.AboutController;
import cn.oyzh.easyzk.controller.MainController;
import cn.oyzh.easyzk.controller.SettingController2;
import cn.oyzh.easyzk.controller.acl.ZKACLAddController;
import cn.oyzh.easyzk.controller.acl.ZKACLUpdateController;
import cn.oyzh.easyzk.controller.connect.ZKAddConnectController;
import cn.oyzh.easyzk.controller.connect.ZKUpdateConnectController;
import cn.oyzh.easyzk.controller.connect.ZKExportConnectController;
import cn.oyzh.easyzk.controller.data.ZKExportDataController;
import cn.oyzh.easyzk.controller.data.ZKImportDataController;
import cn.oyzh.easyzk.controller.data.ZKMigrationDataController;
import cn.oyzh.easyzk.controller.data.ZKMigrationTipsController;
import cn.oyzh.easyzk.controller.data.ZKTransportDataController;
import cn.oyzh.easyzk.controller.node.ZKAddNodeController;
import cn.oyzh.easyzk.controller.node.ZKAuthNodeController;
import cn.oyzh.easyzk.controller.tool.ZKToolController;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.window.ZKShowAboutEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddACLEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddConnectEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddNodeEvent;
import cn.oyzh.easyzk.event.window.ZKShowAuthNodeEvent;
import cn.oyzh.easyzk.event.window.ZKShowExportConnectEvent;
import cn.oyzh.easyzk.event.window.ZKShowExportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowImportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowMigrationDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowSettingEvent;
import cn.oyzh.easyzk.event.window.ZKShowToolEvent;
import cn.oyzh.easyzk.event.window.ZKShowTransportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowUpdateACLEvent;
import cn.oyzh.easyzk.event.window.ZKShowUpdateConnectEvent;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.easyzk.terminal.ZKTerminalManager;
import cn.oyzh.easyzk.zk.ZKSASLUtil;
import cn.oyzh.event.EventFactory;
import cn.oyzh.event.EventListener;
import cn.oyzh.event.EventSubscribe;
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
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.i18n.I18nManager;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2020/9/14
 */
public class EasyZKApp extends FXApplication implements EventListener {

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
            SysConst.storeDir(ZKConst.STORE_PATH);
            JulLog.info("项目启动中...");
            // 储存初始化
            ZKStoreUtil.init();
            // 注册sasl处理器
            ZKSASLUtil.registerConfiguration();
            SysConst.cacheDir(ZKConst.CACHE_PATH);
            FXConst.appIcon(ZKConst.ICON_PATH);
            // 事件总线
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
            // 注册事件处理
            EventListener.super.register();
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
//                FXUtil.runWait(() -> StageManager.showStage(ZKMigrationTipsController.class, primaryStage), 1000);
                this.migrationTips();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("start error", ex);
        }
    }

    @Override
    public void stop() {
        super.stop();
        EventListener.super.unregister();
    }

    @Override
    protected void showMainView() {
//        try {
//            // 显示主页面
//            StageManager.showStage(MainController.class);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            JulLog.warn("showMainView error", ex);
//        }
        this.showMain();
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
            if (OSUtil.isWindows()) {
                TrayManager.init(ZKConst.TRAY_ICON_PATH);
            } else {
                TrayManager.init(ZKConst.ICON_PATH);
            }
            // 设置标题
            TrayManager.setTitle(PROJECT.getName() + " v" + PROJECT.getVersion());
            // 打开主页
            TrayManager.addMenuItem(new DesktopTrayItem("12", this::showMain));
            // 打开设置
            TrayManager.addMenuItem(new SettingTrayItem("12", () -> this.showSetting(null)));
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
            try {
                StageAdapter adapter = StageManager.getStage(MainController.class);
                if (adapter != null) {
                    JulLog.info("front main.");
                    adapter.toFront();
                } else {
                    JulLog.info("show main.");
                    StageManager.showStage(MainController.class);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示设置
     */
    @EventSubscribe
    private void showSetting(ZKShowSettingEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.getStage(SettingController2.class);
                if (adapter != null) {
                    JulLog.info("front setting.");
                    adapter.toFront();
                } else {
                    JulLog.info("show setting.");
                    StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示传输数据
     */
    @EventSubscribe
    private void transportData(ZKShowTransportDataEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.parseStage(ZKTransportDataController.class);
                adapter.setProp("sourceInfo", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示导出数据
     */
    @EventSubscribe
    private void exportData(ZKShowExportDataEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.parseStage(ZKExportDataController.class);
                adapter.setProp("connect", event.data());
                adapter.setProp("nodePath", event.path());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示导入数据
     */
    @EventSubscribe
    private void importData(ZKShowImportDataEvent event) {
        FXUtil.runLater(() -> {
            try {

                StageAdapter adapter = StageManager.parseStage(ZKImportDataController.class);
                adapter.setProp("connect", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示添加连接
     */
    @EventSubscribe
    private void addConnect(ZKShowAddConnectEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.parseStage(ZKAddConnectController.class);
                adapter.setProp("group", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示修改连接
     */
    @EventSubscribe
    private void updateConnect(ZKShowUpdateConnectEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.parseStage(ZKUpdateConnectController.class);
                adapter.setProp("zkConnect", event.data());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 添加zk子节点
     */
    @EventSubscribe
    private void addNode(ZKShowAddNodeEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.parseStage(ZKAddNodeController.class);
                adapter.setProp("zkItem", event.data());
                adapter.setProp("zkClient", event.client());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示认证节点页面
     */
    @EventSubscribe
    private void authNode(ZKShowAuthNodeEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter adapter = StageManager.parseStage(ZKAuthNodeController.class);
                adapter.setProp("zkItem", event.data());
                adapter.setProp("zkClient", event.client());
                adapter.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

//    /**
//     * 显示节点二维码页面
//     */
//    @EventSubscribe
//    private void qrCodeNode(ZKShowQRCodeNodeEvent event) {
//        FXUtil.runLater(() -> {
//            try {
//                StageAdapter fxView = StageManager.parseStage(ZKQRCodeNodeController.class);
//                fxView.setProp("zkNode", event.data());
//                fxView.setProp("nodeData", event.text());
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                MessageBox.exception(ex, I18nHelper.operationException());
//            }
//        });
//    }

    /**
     * 显示工具页面
     */
    @EventSubscribe
    private void tool(ZKShowToolEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(ZKToolController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 添加权限
     */
    @EventSubscribe
    private void addACL(ZKShowAddACLEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter fxView = StageManager.parseStage(ZKACLAddController.class);
                fxView.setProp("zkItem", event.data());
                fxView.setProp("zkClient", event.client());
                fxView.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示修改权限页面
     */
    @EventSubscribe
    private void updateACL(ZKShowUpdateACLEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter fxView = StageManager.parseStage(ZKACLUpdateController.class);
                fxView.setProp("acl", event.acl());
                fxView.setProp("zkItem", event.data());
                fxView.setProp("zkClient", event.client());
                fxView.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示关于页面
     */
    @EventSubscribe
    private void about(ZKShowAboutEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(AboutController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示迁移数据页面
     */
    @EventSubscribe
    private void migrationData(ZKShowMigrationDataEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(ZKMigrationDataController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示迁移提示页面
     */
    private void migrationTips() {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(ZKMigrationTipsController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

    /**
     * 显示导出连接页面
     */
    @EventSubscribe
    private void exportConnect(ZKShowExportConnectEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(ZKExportConnectController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }
}
