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
        // String win_jre_config = packagePath + "win_jre_config.json";
        // String win_jar_config = packagePath + "win_jar_config.json";
        String win_pack_config = packagePath + "win_pack_config.json";
        // String win_jlink_config = packagePath + "win_jlink_config.json";

        Packer packer = new Packer();
        // packer.registerJreHandler(win_jre_config);
        // packer.registerJarHandler(win_jar_config);
        // packer.registerJLinkHandler(win_jlink_config);
        packer.pack(win_pack_config);
    }

    @Test
    public void pkg_easyzk_linux() throws Exception {

        String packagePath = this.getPackagePath();
        // String linux_jre_config = packagePath + "linux_jre_config.json";
        // String linux_jar_config = packagePath + "linux_jar_config.json";
        String linux_pack_config = packagePath + "linux_pack_config.json";

        Packer packer = new Packer();
        // packer.registerJreHandler(linux_jre_config);
        // packer.registerJarHandler(linux_jar_config);
        packer.pack(linux_pack_config);
    }

    @Test
    public void pkg_easyzk_macos() throws Exception {

        String packagePath = this.getPackagePath();
        // String macos_jre_config = packagePath + "macos_jre_config.json";
        // String macos_jar_config = packagePath + "macos_jar_config.json";
        String macos_pack_config = packagePath + "macos_pack_config.json";

        Packer packer = new Packer();
        // packer.registerJreHandler(macos_jre_config);
        // packer.registerJarHandler(macos_jar_config);
        packer.pack(macos_pack_config);
    }

}
