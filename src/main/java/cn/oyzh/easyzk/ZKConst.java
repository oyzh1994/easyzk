package cn.oyzh.easyzk;

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
     * 节点数据缓存路径
     */
    public static final String NODE_DATA_CACHE_PATH = STORE_PATH + "node_data_cache" + File.separator;

    /**
     * icon地址
     */
    public final static String ICON_PATH = "/image/zoo.png";
    // public final static String ICON_PATH = "/image/zookeeper_small.png";

}
