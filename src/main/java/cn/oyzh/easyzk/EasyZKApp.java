package cn.oyzh.easyzk;

import cn.hutool.extra.spring.EnableSpringUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.controller.MainController;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.i18n.I18nManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.spring.SpringApplication;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.theme.ThemeConfig;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;


/**
 * 程序主入口
 *
 * @author oyzh
 * @since 2020/9/14
 */
@SpringBootApplication(scanBasePackages = "cn.oyzh",
        exclude = {
                AopAutoConfiguration.class,
                CacheAutoConfiguration.class,
                DataSourceAutoConfiguration.class,
                MessageSourceAutoConfiguration.class,
                TaskExecutionAutoConfiguration.class,
                TaskSchedulingAutoConfiguration.class,
                SqlInitializationAutoConfiguration.class,
        }
)
@EnableSpringUtil
public class EasyZKApp extends SpringApplication {

    public static void main(String[] args) {
        launchSpring(EasyZKApp.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 应用区域
            I18nManager.apply(ZKSettingStore.SETTING.getLocale());
            // 应用字体
            FontManager.apply(ZKSettingStore.SETTING.fontConfig());
            // 应用主题
            ThemeManager.apply(ZKSettingStore.SETTING.themeConfig());
            // 注册异常处理器
            MessageBox.registerExceptionParser(ZKExceptionParser.INSTANCE);
            // 开始执行业务
            super.start(primaryStage);
            // 显示主页面
            StageUtil.showStage(MainController.class);
            // 开启定期gc
            SystemUtil.gcInterval(60_000);
            // 设置stage全部关闭后不自动销毁进程
            Platform.setImplicitExit(false);
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
