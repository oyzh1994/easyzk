package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.Packer;
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
        String win_jre_config = packagePath + "win_jre_config.json";
        String win_jar_config = packagePath + "win_jar_config.json";
        String win_pack_config = packagePath + "win_pack_config.json";
        String win_jlink_config = packagePath + "win_jlink_config.json";

        Packer packer = new Packer();

        packer.registerEndHandler();
        packer.registerConfHandler();
        packer.registerCompressHandler();
        packer.registerCompressNameHandler();
        packer.registerJreHandler(win_jre_config);
        packer.registerJarHandler(win_jar_config);
        packer.registerJLinkHandler(win_jlink_config);

        packer.pack(win_pack_config);
    }

}
