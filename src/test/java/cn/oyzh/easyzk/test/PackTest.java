package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.Packer;
import cn.oyzh.fx.pkg.jre.JreHandler;
import cn.oyzh.fx.pkg.run.ConfigHandler;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2023/3/8
 */
public class PackTest {

    private String getProjectPath() {
        String projectPath = getClass().getResource("").getPath();
        projectPath = projectPath.substring(1, projectPath.indexOf("/target/"));
        return projectPath;
    }

    private String getPackagePath() {
        return this.getProjectPath() + "/package/";
    }

    @Test
    public void pkg_easyzk_win() throws Exception {

        String packagePath = this.getPackagePath();
        String jre_config = packagePath + "jre_config.json";
        String win_pack_config = packagePath + "win_pack_config.json";

        Packer packer = new Packer();

        JreHandler jreHandler = new JreHandler();
        jreHandler.parse(jre_config);

        ConfigHandler configHandler = new ConfigHandler();

        packer.registerHandler(jreHandler);
        packer.registerHandler(configHandler);
        packer.pack(win_pack_config);
    }

}
