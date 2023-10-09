package cn.oyzh.easyzk;

import cn.oyzh.fx.plus.FXStyle;
import lombok.experimental.UtilityClass;

/**
 * zk样式文件常量对象
 *
 * @author oyzh
 * @since 2023/04/04
 */
@UtilityClass
public class ZKStyle {

    /**
     * 通用样式文件
     */
    public final static String COMMON = FXStyle.JMETRO + ";" + FXStyle.JMETRO_LIGHT_THEME + ";" + FXStyle.BOOTSTRAP_FX;

    /**
     * 主页样式文件
     */
    public final static String MAIN = COMMON + ";/css/main.css";

}
