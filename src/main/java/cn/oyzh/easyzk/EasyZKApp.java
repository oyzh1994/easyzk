package cn.oyzh.easyzk;

import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.controller.MainController;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.i18n.I18nManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.opacity.OpacityManager;
import cn.oyzh.fx.plus.spring.SpringApplication;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.context.annotation.ComponentScan;
// import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration;
// import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
// import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
// import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
// import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
// import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
// import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
// import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
// import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
// import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration;
// import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
// import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2020/9/14
 */
//@SpringBootApplication(scanBasePackages = "cn.oyzh",
//        exclude = {
//                AopAutoConfiguration.class,
//                CacheAutoConfiguration.class,
//                DataSourceAutoConfiguration.class,
//                MessageSourceAutoConfiguration.class,
//                TaskExecutionAutoConfiguration.class,
//                TaskSchedulingAutoConfiguration.class,
//                SqlInitializationAutoConfiguration.class,
//                SpringApplicationAdminJmxAutoConfiguration.class,
//                GsonAutoConfiguration.class,
//                ProjectInfoAutoConfiguration.class,
//                JmxAutoConfiguration.class,
//                SslAutoConfiguration.class,
//        }
//)
@ComponentScan(
        lazyInit = true,
        value = {"cn.oyzh.fx.common", "cn.oyzh.easyzk"}
)
@EnableSpringUtil
public class EasyZKApp extends SpringApplication {

    /**
     * 启动实际
     */
    private final long startAt = System.currentTimeMillis();

    public static void main(String[] args) {
        launchSpring(EasyZKApp.class, args);
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
            StaticLog.info("启动耗时:{}ms-------------------------------", +cost);
            StaticLog.info("内存消耗:{}mb-------------------------------", +usedMemory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        StaticLog.info("EasyZKApp destroyed.");
    }

    @Override
    public void run(String... args) {
        StaticLog.info("EasyZKApp started.");
    }
}
