package cn.oyzh.easyzk.util;

import cn.oyzh.common.util.JarUtil;
import cn.oyzh.common.util.ProcessUtil;
import cn.oyzh.easyzk.EasyZKBootstrap;
import cn.oyzh.fx.plus.window.StageManager;
import lombok.experimental.UtilityClass;

/**
 * @author oyzh
 * @since 2024-12-17
 */
@UtilityClass
public class ZKProcessUtil {

    /**
     * 重启应用
     */
    public static void restartApplication() {
        // jar中
        if (JarUtil.isInJar()) {
            ProcessUtil.restartApplication("org.springframework.boot.loader.JarLauncher", 100, StageManager::exit);
        } else {// 正常环境
            ProcessUtil. restartApplication(EasyZKBootstrap.class, 100, StageManager::exit);
        }
    }
}
