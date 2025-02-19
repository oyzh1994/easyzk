package cn.oyzh.easyzk.util;

import cn.oyzh.common.system.ProcessUtil;
import cn.oyzh.fx.plus.window.StageManager;
import lombok.experimental.UtilityClass;

import java.io.IOException;

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
        try {
            ProcessUtil.restartApplication(100, StageManager::exit);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
