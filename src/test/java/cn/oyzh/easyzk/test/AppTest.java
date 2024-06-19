package cn.oyzh.easyzk.test;

import com.sun.tools.javac.Main;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author oyzh
 * @since 2022/5/18
 */
public class AppTest {

    public static void main(String[] args) throws URISyntaxException {
        // TerminalApp.main(args);
//        AppMain.main(args);
//         URL rootUrl = AppTest.class.getProtectionDomain().getCodeSource().getLocation();
//         File rootDir = new File(rootUrl.getPath());
//         System.out.println(rootDir);

        // 获取名为"PATH"的环境变量
        String path = System.getenv("PATH");
        System.out.println("PATH环境变量的值: " + path);

        // 你可以获取任何其他环境变量的值，只需要替换环境变量的名称即可
        String mavenHome = System.getenv("MAVEN_HOME"); // 假设你设置了一个名为MAVEN_HOME的环境变量
        if (mavenHome != null) {
            System.out.println("MAVEN_HOME环境变量的值: " + mavenHome);
        } else {
            System.out.println("MAVEN_HOME环境变量未设置");
        }
    }

}
