package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.jlink.JLinkConfig;
import cn.oyzh.fx.pkg.jlink.JLinkHandler;
import org.junit.Test;

import java.io.IOException;

/**
 * @author oyzh
 * @since 2023/3/9
 */
public class JLinkTest {

    private String getProjectPath() {
        String projectPath = getClass().getResource("").getPath();
        projectPath = projectPath.substring(1, projectPath.indexOf("/target/"));
        return projectPath;
    }

    private String getPackagePath() {
        return this.getProjectPath() + "/package";
    }

    @Test
    public void test_jlink() throws IOException, InterruptedException {
        String packagePath = this.getPackagePath();
        String jlinkConfig = packagePath+"/win_jlink_config.json";
        JLinkConfig config = JLinkConfig.fromConfig(jlinkConfig);
        JLinkHandler jLinkHandler = new JLinkHandler();
        jLinkHandler.exec(config);
    }
}
