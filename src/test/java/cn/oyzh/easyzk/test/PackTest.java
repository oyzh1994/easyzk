package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.Packer;
import org.junit.Test;

import java.util.List;

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
    public void easyzk_win_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String win_pack_config = packagePath + "win_amd64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(win_pack_config);
    }

    @Test
    public void easyzk_linux_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String linux_pack_config = packagePath + "linux_amd64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(linux_pack_config);
    }

    @Test
    public void easyzk_linux_arm64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String linux_pack_config = packagePath + "linux_arm64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(linux_pack_config);
    }

    @Test
    public void easyzk_macos_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String macos_pack_config = packagePath + "macos_amd64_pack_config.json";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(macos_pack_config);
    }

    @Test
    public void easyzk_all_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String win_amd64_pack_config = packagePath + "win_amd64_pack_config.json";
        String linux_amd64_pack_config = packagePath + "linux_amd64_pack_config.json";
        String linux_arm64_pack_config = packagePath + "linux_arm64_pack_config.json";
        String macos_amd64_pack_config = packagePath + "macos_amd64_pack_config.json";

        String baseDir = "D:\\Workspaces\\OYZH\\fx-base\\";
        String projectDir = "D:\\Workspaces\\OYZH\\easyzk\\";

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.registerMvnHandler(projectDir, List.of(baseDir));
        packer.pack(win_amd64_pack_config);
        packer.pack(linux_amd64_pack_config);
        packer.pack(linux_arm64_pack_config);
        packer.pack(macos_amd64_pack_config);
    }

}
