package cn.oyzh.easyzk;

import cn.oyzh.common.util.JarUtil;

import java.io.File;

/**
 * zk常量对象
 *
 * @author oyzh
 * @since 2022/8/26
 */

public class ZKConst {

//    /**
//     * 数据保存路径
//     */
//    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easyzk" + File.separator;
//
//    /**
//     * 缓存保存路径
//     */
//    public static final String CACHE_PATH = STORE_PATH + "cache" + File.separator;

//    /**
//     * 节点缓存路径
//     */
//    public static final String NODE_CACHE_PATH = CACHE_PATH + "node_cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/zoo_no_bg.png";

    /**
     * 托盘图标，windows专用
     */
    public final static String ICON_24_PATH = "/image/zoo_24.png";

    /**
     * 任务栏图标，windows专用
     */
    public final static String ICON_32_PATH = "/image/zoo_32.png";

    /**
     * 获取存储路径
     *
     * @return 存储路径
     */
    public static String getStorePath() {
        if (JarUtil.isInJar()) {
            return System.getProperty("user.home") + File.separator + ".easyzk" + File.separator;
        }
        return System.getProperty("user.home") + File.separator + ".easyzk_dev" + File.separator;
    }

    /**
     * 获取缓存路径
     *
     * @return 缓存路径
     */
    public static String getCachePath() {
        return getStorePath() + "cache" + File.separator;
    }

    /**
     * 获取节点缓存路径
     *
     * @return 节点缓存路径
     */
    public static String getNodeCachePath() {
        return getCachePath() + "node_cache" + File.separator;
    }

}
