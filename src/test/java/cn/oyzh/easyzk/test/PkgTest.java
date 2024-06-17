// package cn.oyzh.easyzk.test;
//
// import cn.oyzh.fx.pkg.clip.clipper.JarClipConfig;
// import cn.oyzh.fx.pkg.clip.clipper.JreClipConfig;
// import cn.oyzh.fx.pkg.config.ConfigParser;
// import cn.oyzh.fx.pkg.config.PlatformConfig;
// import cn.oyzh.fx.pkg.jlink.JLinkConfig;
// import cn.oyzh.fx.pkg.packager.BasePackager;
// import cn.oyzh.fx.pkg.packager.LinuxPackager;
// import cn.oyzh.fx.pkg.packager.MacPackager;
// import cn.oyzh.fx.pkg.packager.WinPackager;
// import cn.oyzh.fx.pkg.util.PkgUtil;
// import org.junit.Test;
//
// /**
//  * @author oyzh
//  * @since 2023/3/8
//  */
// public class PkgTest {
//
//     private String getProjectPath() {
//         String projectPath = getClass().getResource("").getPath();
//         projectPath = projectPath.substring(1, projectPath.indexOf("/target/"));
//         return projectPath;
//     }
//
//     private String getPackagePath() {
//         return this.getProjectPath() + "/package/";
//     }
//
//     @Test
//     public void pkg_easyzk_macos() throws Exception {
//         String packagePath = this.getPackagePath();
//         String global_config = packagePath + "global_pkg_config.json";
//         String macos_pkg_config = packagePath + "macos_pkg_config.json";
//         ConfigParser parser = new ConfigParser();
//         parser.loadConfig(global_config, macos_pkg_config);
//         for (String platform : parser.getPlatforms()) {
//             PlatformConfig platformConfig = parser.getCrossPlatformConfig(platform);
//             BasePackager packager = PkgUtil.getPackager(platform);
//             packager.setPlatformConfig(platformConfig);
//             packager.setGlobalConfig(parser.getGlobalConfig());
//             packager.pack();
//         }
//     }
//
//     @Test
//     public void pkg_easyzk_win() throws Exception {
//         String packagePath = this.getPackagePath();
//         String global_config = packagePath + "global_pkg_config.json";
//         String win_pkg_config = packagePath + "win_pkg_config.json";
//         ConfigParser parser = new ConfigParser();
//         parser.loadConfig(global_config, win_pkg_config);
//         for (String platform : parser.getPlatforms()) {
//             PlatformConfig platformConfig = parser.getCrossPlatformConfig(platform);
//             BasePackager packager = PkgUtil.getPackager(platform);
//             packager.setPlatformConfig(platformConfig);
//             packager.setGlobalConfig(parser.getGlobalConfig());
//             packager.pack();
//         }
//     }
//
//     @Test
//     public void pkg_easyzk_linux() throws Exception {
//         String packagePath = this.getPackagePath();
//         String global_config = packagePath + "global_pkg_config.json";
//         String linux_pkg_config = packagePath + "linux_pkg_config.json";
//         ConfigParser parser = new ConfigParser();
//         parser.loadConfig(global_config, linux_pkg_config);
//         for (String platform : parser.getPlatforms()) {
//             PlatformConfig platformConfig = parser.getCrossPlatformConfig(platform);
//             BasePackager packager = PkgUtil.getPackager(platform);
//             packager.setPlatformConfig(platformConfig);
//             packager.setGlobalConfig(parser.getGlobalConfig());
//             packager.pack();
//         }
//     }
//
//     @Test
//     public void pkg_easyzk_all_platform() throws Exception {
//         this.pkg_easyzk_macos();
//         this.pkg_easyzk_linux();
//         this.pkg_easyzk_win();
//     }
// }
