package cn.oyzh.easyzk;

import cn.oyzh.easyzk.controller.MainController;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.fx.common.SysConst;
import cn.oyzh.fx.common.date.LocalZoneRulesProvider;
import cn.oyzh.fx.common.log.JulLog;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.ext.ApplicationExt;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.i18n.I18nManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.terminal.TerminalConst;
import javafx.application.Platform;
import javafx.stage.Stage;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2020/9/14
 */
// @ComponentScan(
//         lazyInit = true,
//         value = {"cn.oyzh.fx.common", "cn.oyzh.easyzk"}
// )
// @EnableSpringUtil
public class EasyZKApp extends ApplicationExt {

    /**
     * 启动实际
     */
    private final long startAt = System.currentTimeMillis();

    public static void main(String[] args) {
        // 初始化时区处理器
        System.setProperty(SysConst.CACHE_DIR, ZKConst.CACHE_PATH);
        System.setProperty(TerminalConst.SCAN_BASE, "cn.oyzh.easyzk.terminal");
        System.setProperty("java.time.zone.DefaultZoneRulesProvider", LocalZoneRulesProvider.class.getName());
        launch(EasyZKApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 储存初始化
            ZKStoreUtil.init();
            // 储存迁移
            ZKStoreUtil.migration();
            // 应用区域
            I18nManager.apply(ZKSettingStore2.SETTING.getLocale());
            // 应用字体
            FontManager.apply(ZKSettingStore2.SETTING.fontConfig());
            // 应用主题
            ThemeManager.apply(ZKSettingStore2.SETTING.themeConfig());
            // 应用透明度
            OpacityManager.apply(ZKSettingStore2.SETTING.getOpacity());
            // 注册异常处理器
            MessageBox.registerExceptionParser(ZKExceptionParser.INSTANCE);
            // 开始执行业务
            super.start(primaryStage);
            // 显示主页面
            StageManager.showStage(MainController.class);
            // 开启定期gc
            SystemUtil.gcInterval(60_000);
            // 设置stage全部关闭后不自动销毁进程
            Platform.setImplicitExit(false);
            // 启动耗时
            long cost = System.currentTimeMillis() - this.startAt;
            // 内存消耗
            double usedMemory = SystemUtil.getUsedMemory();
            JulLog.info("启动耗时:{}ms-------------------------------", +cost);
            JulLog.info("内存消耗:{}mb-------------------------------", +usedMemory);
            JulLog.info("EasyZKApp start.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        super.stop();
        JulLog.info("EasyZKApp stop");
    }

    // @Override
    // public void destroy() {
    //     JulLog.info("EasyZKApp destroyed.");
    // }
    //
    // @Override
    // public void run(String... args) {
    //     JulLog.info("EasyZKApp started.");
    // }
}
