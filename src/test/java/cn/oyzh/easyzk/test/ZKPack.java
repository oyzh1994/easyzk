package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.Packer;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2023/3/8
 */
public class ZKPack {

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
        String getProjectPath = this.getProjectPath();
        Map<String, Object> properties = new HashMap<>();
        properties.put("projectPath", getProjectPath);

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.registerJdepsHandler();
        packer.pack(win_pack_config, properties);
    }

    @Test
    public void easyzk_linux_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String linux_pack_config = packagePath + "linux_amd64_pack_config.json";
        String getProjectPath = this.getProjectPath();
        Map<String, Object> properties = new HashMap<>();
        properties.put("projectPath", getProjectPath);

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(linux_pack_config, properties);
    }

    @Test
    public void easyzk_linux_arm64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String linux_pack_config = packagePath + "linux_arm64_pack_config.json";
        String getProjectPath = this.getProjectPath();
        Map<String, Object> properties = new HashMap<>();
        properties.put("projectPath", getProjectPath);

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(linux_pack_config, properties);
    }

    @Test
    public void easyzk_macos_amd64_pack() throws Exception {
        String packagePath = this.getPackagePath();
        String macos_pack_config = packagePath + "macos_amd64_pack_config.json";
        String getProjectPath = this.getProjectPath();
        Map<String, Object> properties = new HashMap<>();
        properties.put("projectPath", getProjectPath);

        Packer packer = new Packer();
        packer.registerProjectHandler();
        packer.pack(macos_pack_config, properties);
    }

    @Test
    public void easyzk_all_pack() throws Exception {
        String projectPath = this.getProjectPath();
        String packagePath = this.getPackagePath();
        String win_amd64_pack_config = packagePath + "win_amd64_pack_config.json";
        String linux_amd64_pack_config = packagePath + "linux_amd64_pack_config.json";
        String linux_arm64_pack_config = packagePath + "linux_arm64_pack_config.json";
        String macos_amd64_pack_config = packagePath + "macos_amd64_pack_config.json";
        Map<String, Object> properties = new HashMap<>();
        properties.put("projectPath", projectPath);
        Packer packer = new Packer();
        packer.registerProjectHandler();
        // String baseDir1 = projectPath.replace("easyzk", "base");
        // String baseDir2 = projectPath.replace("easyzk", "fx-base");
        // String projectDir = projectPath;
        // packer.registerMvnHandler(projectDir, List.of(baseDir1, baseDir2));
        packer.pack(win_amd64_pack_config, properties);
        packer.pack(linux_amd64_pack_config, properties);
        packer.pack(linux_arm64_pack_config, properties);
        packer.pack(macos_amd64_pack_config, properties);
    }
}
