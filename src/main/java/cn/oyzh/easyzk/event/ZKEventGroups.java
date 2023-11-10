package cn.oyzh.easyzk.event;

import lombok.experimental.UtilityClass;

/**
 * zk事件分组
 *
 * @author oyzh
 * @since 2023/9/18
 */
@UtilityClass
public class ZKEventGroups {

    /**
     * 节点消息
     */
    public static final String NODE_MSG = "NODE_MSG";

    /**
     * 节点操作
     */
    public static final String NODE_ACTION = "NODE_ACTION";

    /**
     * 树操作
     */
    public static final String TREE_ACTION = "TREE_ACTION";

    /**
     * 信息操作
     */
    public static final String INFO_ACTION = "INFO_ACTION";

    /**
     * 终端操作
     */
    public static final String TERMINAL_ACTION = "TERMINAL_ACTION";

    /**
     * 连接操作
     */
    public static final String CONNECTION_ACTION = "CONNECTION_ACTION";

    /**
     * 认证操作
     */
    public static final String AUTH_ACTION = "AUTH_ACTION";

    /**
     * 过滤操作
     */
    public static final String FILTER_ACTION = "FILTER_ACTION";

}
