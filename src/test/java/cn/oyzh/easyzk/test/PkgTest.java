package cn.oyzh.easyzk.test;

import cn.oyzh.fx.pkg.clip.clipper.JarClipConfig;
import cn.oyzh.fx.pkg.clip.clipper.JreClipConfig;
import cn.oyzh.fx.pkg.jlink.JLinkConfig;
import cn.oyzh.fx.pkg.packager.LinuxPackager;
import cn.oyzh.fx.pkg.packager.LinuxPkgConfig;
import cn.oyzh.fx.pkg.packager.MacPackager;
import cn.oyzh.fx.pkg.packager.MacPkgConfig;
import cn.oyzh.fx.pkg.packager.WinPackager;
import cn.oyzh.fx.pkg.packager.WinPkgConfig;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2023/3/8
 */
public class PkgTest {

    private String getProjectPath() {
        String projectPath = getClass().getResource("").getPath();
        projectPath = projectPath.substring(1, projectPath.indexOf("/target/"));
        return projectPath;
    }

    private String getPackagePath() {
        return this.getProjectPath() + "/package";
    }

    @Test
    public void pkg_easyzk_macos() throws Exception {
        String packagePath = this.getPackagePath();
        String pkgConfig = packagePath + "/mac_pkg_config.json";
        String jarClipConfigPath = packagePath + "/mac_jar_clip_config.json";
        String jreClipConfigPath = packagePath + "/mac_jre_clip_config.json";

        //String pkgConfig = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\mac_pkg_config.json";
        //String jarClipConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\mac_jar_clip_config.json";
        //String jreClipConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\mac_jre_clip_config.json";

        MacPkgConfig config = MacPkgConfig.fromConfig(pkgConfig);
        JarClipConfig jarClipConfig = JarClipConfig.fromConfig(jarClipConfigPath);
        JreClipConfig jreClipConfig = JreClipConfig.fromConfig(jreClipConfigPath);

        MacPackager packager = new MacPackager();
        packager.setPkgConfig(config);
        packager.setJreClipConfig(jreClipConfig);
        packager.setJarClipConfig(jarClipConfig);

        packager.pack();
    }

    @Test
    public void pkg_easyzk_win() throws Exception {
        String packagePath = this.getPackagePath();
        String pkgConfig = packagePath + "/win_pkg_config.json";
        String jlinkConfigPath = packagePath + "/win_jlink_config.json";
        String jarClipConfigPath = packagePath + "/win_jar_clip_config.json";
        String jreClipConfigPath = packagePath + "/win_jre_clip_config.json";

        //String pkgConfig = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\win_pkg_config.json";
        //String jlinkConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\win_jlink_config.json";
        //String jarClipConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\win_jar_clip_config.json";
        //String jreClipConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\win_jre_clip_config.json";

        WinPkgConfig config = WinPkgConfig.fromConfig(pkgConfig);
        JLinkConfig jLinkConfig = JLinkConfig.fromConfig(jlinkConfigPath);
        JarClipConfig jarClipConfig = JarClipConfig.fromConfig(jarClipConfigPath);
        JreClipConfig jreClipConfig = JreClipConfig.fromConfig(jreClipConfigPath);

        WinPackager packager = new WinPackager();
        packager.setPkgConfig(config);
        packager.setJLinkConfig(jLinkConfig);
        packager.setJreClipConfig(jreClipConfig);
        packager.setJarClipConfig(jarClipConfig);

        packager.pack();
    }

    @Test
    public void pkg_easyzk_linux() throws Exception {
        String packagePath = this.getPackagePath();
        String pkgConfig = packagePath + "/linux_pkg_config.json";
        String jarClipConfigPath = packagePath + "/linux_jar_clip_config.json";
        String jreClipConfigPath = packagePath + "/linux_jre_clip_config.json";

        //String pkgConfig = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\linux_pkg_config.json";
        //String jarClipConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\linux_jar_clip_config.json";
        //String jreClipConfigPath = "D:\\Workspaces\\OYZH\\Web\\easypanel\\easyzk\\package\\linux_jre_clip_config.json";

        LinuxPkgConfig config = LinuxPkgConfig.fromConfig(pkgConfig);
        JarClipConfig jarClipConfig = JarClipConfig.fromConfig(jarClipConfigPath);
        JreClipConfig jreClipConfig = JreClipConfig.fromConfig(jreClipConfigPath);

        LinuxPackager packager = new LinuxPackager();
        packager.setPkgConfig(config);
        packager.setJreClipConfig(jreClipConfig);
        packager.setJarClipConfig(jarClipConfig);

        packager.pack();
    }

    @Test
    public void pkg_easyzk_all_platform() throws Exception {
        this.pkg_easyzk_macos();
        this.pkg_easyzk_linux();
        this.pkg_easyzk_win();
    }
}
