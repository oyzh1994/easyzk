package cn.oyzh.easyzk.controller;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyzk.controller.acl.ZKACLAddController;
import cn.oyzh.easyzk.controller.acl.ZKACLUpdateController;
import cn.oyzh.easyzk.controller.connect.ZKUpdateConnectController;
import cn.oyzh.easyzk.controller.data.ZKMigrationDataController;
import cn.oyzh.easyzk.controller.data.ZKMigrationTipsController;
import cn.oyzh.easyzk.controller.node.ZKAuthNodeController;
import cn.oyzh.easyzk.controller.connect.ZKAddConnectController;
import cn.oyzh.easyzk.controller.data.ZKExportDataController;
import cn.oyzh.easyzk.controller.data.ZKImportDataController;
import cn.oyzh.easyzk.controller.data.ZKTransportDataController;
import cn.oyzh.easyzk.controller.node.ZKAddNodeController;
import cn.oyzh.easyzk.controller.node.ZKQRCodeNodeController;
import cn.oyzh.easyzk.controller.tool.ZKToolController;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.window.ZKShowAboutEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddACLEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddConnectEvent;
import cn.oyzh.easyzk.event.window.ZKShowAuthNodeEvent;
import cn.oyzh.easyzk.event.window.ZKShowExportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowImportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowMainEvent;
import cn.oyzh.easyzk.event.window.ZKShowMigrationDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddNodeEvent;
import cn.oyzh.easyzk.event.window.ZKShowQRCodeNodeEvent;
import cn.oyzh.easyzk.event.window.ZKShowSettingEvent;
import cn.oyzh.easyzk.event.window.ZKShowToolEvent;
import cn.oyzh.easyzk.event.window.ZKShowTransportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowUpdateACLEvent;
import cn.oyzh.easyzk.event.window.ZKShowUpdateConnectEvent;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.ParentStageController;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.titlebar.TitleBar;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
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
        fullScreenAble = true,
        alwaysOnTopAble = true,
        value = FXConst.FXML_PATH + "main.fxml"
)
public class MainController extends ParentStageController {

    /**
     * 项目信息
     */
    private final Project project = Project.load();

    /**
     * 头部页面
     */
    @FXML
    private HeaderController3 headerController;

    /**
     * zk主页业务
     */
    @FXML
    private ZKMainController zkMainController;

    /**
     * zk相关配置
     */
    private final ZKSetting setting = ZKSettingStore.SETTING;

    /**
     * 设置存储
     */
    private final ZKSettingStore settingStore = ZKSettingStore.INSTANCE;

    @Override
    public List<? extends StageController> getSubControllers() {
        return Arrays.asList(this.zkMainController, this.headerController);
//        return Collections.singletonList(this.zkMainController);
    }

    @Override
    public void onWindowCloseRequest(WindowEvent event) {
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
                TrayManager.show();
            } else {
                JulLog.error("tray not support!");
                // MessageBox.warn(I18nHelper.trayNotSupport());
            }
        }
    }

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
            this.settingStore.replace(this.setting);
        }
        // 关闭托盘
        TrayManager.destroy();
        super.onSystemExit();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        try {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("onStageInitialize error", ex);
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        try {
            super.onWindowShown(event);
            TitleBar titleBar = this.stage.getTitleBar();
            // 加载标题
            if (titleBar != null && !titleBar.isHasContent()) {
                titleBar.loadContent("/fxml/header2.fxml");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JulLog.warn("onStageInitialize error", ex);
        }
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.main");
    }

    /**
     * 显示主页
     */
    @EventSubscribe
    private void showMain(ZKShowMainEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.getStage(MainController.class);
            if (adapter != null) {
                JulLog.info("front main.");
                adapter.toFront();
            } else {
                JulLog.info("show main.");
                StageManager.showStage(MainController.class);
            }
        });
    }

    /**
     * 显示设置
     */
    @EventSubscribe
    private void showSetting(ZKShowSettingEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.getStage(SettingController2.class);
            if (adapter != null) {
                JulLog.info("front setting.");
                adapter.toFront();
            } else {
                JulLog.info("show setting.");
                StageManager.showStage(SettingController2.class, StageManager.getPrimaryStage());
            }
        });
    }

    /**
     * 显示传输数据
     */
    @EventSubscribe
    private void transportData(ZKShowTransportDataEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKTransportDataController.class);
            adapter.setProp("sourceInfo", event.data());
            adapter.display();
        });
    }

    /**
     * 显示导出数据
     */
    @EventSubscribe
    private void exportData(ZKShowExportDataEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKExportDataController.class);
            adapter.setProp("connect", event.data());
            adapter.setProp("nodePath", event.path());
            adapter.display();
        });
    }

    /**
     * 显示导入数据
     */
    @EventSubscribe
    private void importData(ZKShowImportDataEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKImportDataController.class);
            adapter.setProp("connect", event.data());
            adapter.display();
        });
    }

    /**
     * 显示添加连接
     */
    @EventSubscribe
    private void addConnect(ZKShowAddConnectEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKAddConnectController.class);
            adapter.setProp("group", event.data());
            adapter.display();
        });
    }

    /**
     * 显示修改连接
     */
    @EventSubscribe
    private void updateConnect(ZKShowUpdateConnectEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKUpdateConnectController.class);
            adapter.setProp("zkConnect", event.data());
            adapter.display();
        });
    }

    /**
     * 添加zk子节点
     */
    @EventSubscribe
    private void addNode(ZKShowAddNodeEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKAddNodeController.class);
            adapter.setProp("zkItem", event.data());
            adapter.setProp("zkClient", event.client());
            adapter.display();
        });
    }

    /**
     * 显示认证节点页面
     */
    @EventSubscribe
    private void authNode(ZKShowAuthNodeEvent event) {
        FXUtil.runLater(() -> {
            StageAdapter adapter = StageManager.parseStage(ZKAuthNodeController.class);
            adapter.setProp("zkItem", event.data());
            adapter.setProp("zkClient", event.client());
            adapter.display();
        });
    }

    /**
     * 显示节点二维码页面
     */
    @EventSubscribe
    private void qrCodeNode(ZKShowQRCodeNodeEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageAdapter fxView = StageManager.parseStage(ZKQRCodeNodeController.class);
                fxView.setProp("zkNode", event.data());
                fxView.setProp("nodeData", event.text());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

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
    @EventSubscribe
    private void migrationTips(ZKShowMigrationDataEvent event) {
        FXUtil.runLater(() -> {
            try {
                StageManager.showStage(ZKMigrationTipsController.class, StageManager.getPrimaryStage());
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex, I18nHelper.operationException());
            }
        });
    }

}
