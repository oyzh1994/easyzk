package cn.oyzh.easyzk;

import cn.oyzh.fx.plus.util.FXUtil;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * zk常量对象
 *
 * @author oyzh
 * @since 2022/8/26
 */
@UtilityClass
public class ZKConst {

    /**
     * fxml基础地址
     */
    public final static String FXML_BASE_PATH = "/views/";

    /**
     * 数据保存路径
     */
    public static final String STORE_PATH = System.getProperty("user.home") + File.separator + ".easyzk" + File.separator;

    /**
     * 缓存保存路径
     */
    public static final String CACHE_PATH = STORE_PATH + "cache" + File.separator;

    /**
     * 节点缓存路径
     */
    public static final String NODE_CACHE_PATH = CACHE_PATH + "node_cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/zoo_no_bg.png";
}
