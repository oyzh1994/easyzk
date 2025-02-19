package cn.oyzh.easyzk.test;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.system.RuntimeUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author oyzh
 * @since 2024-12-18
 */
public class RuntimeTest {

    @Test
    public void test2() {
        String exec = "D:\\Package\\EasyZK\\2.0.0\\EasyZK_v2.0.0_win_amd64_20241217_build\\EasyZK_v2.0.0\\EasyZK_v2.0.0.exe";
        RuntimeUtil.execAndWait(new String[]{exec});
    }

    @Test
    public void test3() {
        TaskManager.startDelay(() -> {
            String exec = "D:\\Package\\EasyZK\\2.0.0\\EasyZK_v2.0.0_win_amd64_20241217_build\\EasyZK_v2.0.0\\EasyZK_v2.0.0.exe";
            RuntimeUtil.execAndWait(new String[]{exec});
        }, 500);
    }

    @Test
    public void test4() throws IOException, InterruptedException {
        File dir = new File("D:\\Package\\EasyZK\\2.0.0\\EasyZK_v2.0.0_win_amd64_20241217_build\\EasyZK_v2.0.0");
        String exec = dir.getPath() + "\\restart.bat";
        Process process=   Runtime.getRuntime().exec(exec);
        // // 获取bat脚本的输出
        // BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        // String line;
        // while ((line = reader.readLine()) != null) {
        //     System.out.println(line);
        // }
        //
        // // 等待bat脚本执行完毕，并获取其退出值
        // int exitCode = process.waitFor();
        // System.out.println(exitCode);
    }

    @Test
    public void test5() throws IOException, InterruptedException {
        String restartCommand = "D:\\Libraries\\JDKs\\jdk-23_windows-x64_bin\\jdk-23.0.1/bin/javaw.exe -cp \"D:\\Workspaces\\OYZH\\easyzk\\target\\classes;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\poi\\poi\\5.3.0\\poi-5.3.0.jar;C:\\Users\\oyzh\\.m2\\repository\\commons-codec\\commons-codec\\1.17.0\\commons-codec-1.17.0.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\commons\\commons-collections4\\4.4\\commons-collections4-4.4.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\commons\\commons-math3\\3.6.1\\commons-math3-3.6.1.jar;C:\\Users\\oyzh\\.m2\\repository\\commons-io\\commons-io\\2.16.1\\commons-io-2.16.1.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\zaxxer\\SparseBitSet\\1.3\\SparseBitSet-1.3.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\logging\\log4j\\log4j-api\\2.23.1\\log4j-api-2.23.1.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\poi\\poi-ooxml\\5.3.0\\poi-ooxml-5.3.0.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\poi\\poi-ooxml-lite\\5.3.0\\poi-ooxml-lite-5.3.0.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\xmlbeans\\xmlbeans\\5.2.1\\xmlbeans-5.2.1.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\commons\\commons-compress\\1.26.2\\commons-compress-1.26.2.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\commons\\commons-lang3\\3.14.0\\commons-lang3-3.14.0.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\github\\virtuald\\curvesapi\\1.08\\curvesapi-1.08.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\google\\zxing\\core\\3.5.3\\core-3.5.3.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\curator\\curator-recipes\\4.3.0\\curator-recipes-4.3.0.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\curator\\curator-framework\\4.3.0\\curator-framework-4.3.0.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\apache\\curator\\curator-client\\4.3.0\\curator-client-4.3.0.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\slf4j\\slf4j-api\\1.7.25\\slf4j-api-1.7.25.jar;C:\\Users\\oyzh\\.m2\\repository\\commons-cli\\commons-cli\\1.5.0\\commons-cli-1.5.0.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\google\\guava\\guava\\33.1.0-jre\\guava-33.1.0-jre.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\google\\guava\\failureaccess\\1.0.2\\failureaccess-1.0.2.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\google\\guava\\listenablefuture\\9999.0-empty-to-avoid-conflict-with-guava\\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;D:\\Workspaces\\OYZH\\base\\ssh\\target\\classes;D:\\Workspaces\\OYZH\\base\\event\\target\\classes;D:\\Workspaces\\OYZH\\base\\common\\target\\classes;D:\\Workspaces\\OYZH\\base\\store\\target\\classes;D:\\Workspaces\\OYZH\\fx-base\\fx-rich\\target\\classes;D:\\Workspaces\\OYZH\\fx-base\\fx-plus\\target\\classes;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-base\\24-ea+15\\javafx-base-24-ea+15.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-base\\24-ea+15\\javafx-base-24-ea+15-win.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-fxml\\24-ea+15\\javafx-fxml-24-ea+15.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-fxml\\24-ea+15\\javafx-fxml-24-ea+15-win.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-controls\\24-ea+15\\javafx-controls-24-ea+15.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-controls\\24-ea+15\\javafx-controls-24-ea+15-win.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-graphics\\24-ea+15\\javafx-graphics-24-ea+15.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-graphics\\24-ea+15\\javafx-graphics-24-ea+15-win.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-graphics\\24-ea+15\\javafx-graphics-24-ea+15-linux.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-graphics\\24-ea+15\\javafx-graphics-24-ea+15-linux-aarch64.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\openjfx\\javafx-graphics\\24-ea+15\\javafx-graphics-24-ea+15-mac.jar;D:\\Workspaces\\OYZH\\base\\i18n\\target\\classes;C:\\Users\\oyzh\\.m2\\repository\\io\\github\\mkpaz\\atlantafx-base\\2.0.1\\atlantafx-base-2.0.1.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\fxmisc\\richtext\\richtextfx\\0.11.3\\richtextfx-0.11.3.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\reactfx\\reactfx\\2.0-M5\\reactfx-2.0-M5.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\fxmisc\\undo\\undofx\\2.1.1\\undofx-2.1.1.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\fxmisc\\flowless\\flowless\\0.7.3\\flowless-0.7.3.jar;C:\\Users\\oyzh\\.m2\\repository\\org\\fxmisc\\wellbehaved\\wellbehavedfx\\0.3.3\\wellbehavedfx-0.3.3.jar;D:\\Workspaces\\OYZH\\fx-base\\fx-gui\\target\\classes;D:\\Workspaces\\OYZH\\fx-base\\fx-terminal\\target\\classes;C:\\Users\\oyzh\\.m2\\repository\\com\\h2database\\h2\\2.3.232\\h2-2.3.232.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\github\\mwiede\\jsch\\0.2.17\\jsch-0.2.17.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\alibaba\\fastjson\\2.0.52\\fastjson-2.0.52.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\alibaba\\fastjson2\\fastjson2-extension\\2.0.52\\fastjson2-extension-2.0.52.jar;C:\\Users\\oyzh\\.m2\\repository\\com\\alibaba\\fastjson2\\fastjson2\\2.0.52\\fastjson2-2.0.52.jar;C:\\Users\\oyzh\\AppData\\Local\\Programs\\IntelliJ IDEA Ultimate 2\\lib\\idea_rt.jar\" cn.oyzh.easyzk.EasyZKBootstrap";
        Process process=   Runtime.getRuntime().exec(restartCommand);
    }
}
