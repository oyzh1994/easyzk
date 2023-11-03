package cn.oyzh.easyzk.event;

import lombok.experimental.UtilityClass;

/**
 * zk事件类型
 *
 * @author oyzh
 * @since 2022/6/2
 */
@UtilityClass
public class ZKEventTypes {

    /**
     * 应用退出事件
     */
    public static final String APP_EXIT = "APP_EXIT";

    /**
     * zk认证信息添加事件
     */
    public static final String ZK_AUTH_ADDED = "ZK_AUTH_ADDED";

    /**
     * zk认证失败事件
     */
    public static final String ZK_AUTH_FAIL = "ZK_AUTH_FAIL";

    /**
     * zk认证成功事件
     */
    public static final String ZK_AUTH_SUCCESS = "ZK_AUTH_SUCCESS";

    /**
     * zk认证启用事件
     */
    public static final String ZK_AUTH_ENABLE = "ZK_AUTH_ENABLE";

    /**
     * zk节点新增事件(来源于操作)
     */
    public static final String ZK_NODE_ADD = "ZK_NODE_ADD";

    /**
     * zk节点已新增事件
     */
    public static final String ZK_NODE_ADDED = "ZK_NODE_ADDED";

    /**
     * zk节点修改事件(来源于操作)
     */
    public static final String ZK_NODE_UPDATE = "ZK_NODE_UPDATE";

    /**
     * zk节点已修改事件(来源于消息)
     */
    public static final String ZK_NODE_UPDATED = "ZK_NODE_UPDATED";

    /**
     * zk节点删除事件(来源于操作)
     */
    public static final String ZK_NODE_DELETE = "ZK_NODE_DELETE";

    /**
     * zk节点已删除事件(来源于消息)
     */
    public static final String ZK_NODE_DELETED = "ZK_NODE_DELETED";


    /**
     * zk连接初始化完成事件
     */
    public static final String ZK_INITIALIZED = "ZK_INITIALIZED";

    /**
     * zk连接成功事件
     */
    public static final String ZK_CONNECTION_CONNECTED = "ZK_CONNECTION_CONNECTED";

    /**
     * zk连接丢失事件
     */
    public static final String ZK_CONNECTION_LOST = "ZK_CONNECTION_LOST";

    /**
     * zk连接关闭事件
     */
    public static final String ZK_CONNECTION_CLOSED = "ZK_CONNECTION_CLOSED";

    /**
     * zk树子节点变化事件
     */
    public static final String TREE_CHILD_CHANGED = "TREE_CHILD_CHANGED";

    /**
     * zk树过子节点滤事件
     */
    public static final String TREE_CHILD_FILTER = "TREE_CHILD_FILTER";

    /**
     * zk树图标变化事件
     */
    public static final String TREE_GRAPHIC_CHANGED = "TREE_GRAPHIC_CHANGED";

    /**
     * zk树图标颜色变化事件
     */
    public static final String TREE_GRAPHIC_COLOR_CHANGED = "TREE_GRAPHIC_COLOR_CHANGED";

    /**
     * zk连接暂停事件
     */
    public static final String ZK_CONNECTION_SUSPENDED = "ZK_CONNECTION_SUSPENDED";

    /**
     * zk连接重连事件
     */
    public static final String ZK_CONNECTION_RECONNECTED = "ZK_CONNECTION_RECONNECTED";

    /**
     * zk信息新增
     */
    public static final String ZK_INFO_ADDED = "ZK_INFO_ADDED";

    /**
     * zk信息修改
     */
    public static final String ZK_INFO_UPDATED = "ZK_INFO_UPDATED";

    /**
     * zk删除修改
     */
    public static final String ZK_INFO_DELETED = "ZK_INFO_DELETED";


    /**
     * zk过滤配置新增
     */
    public static final String ZK_FILTER_ADDED = "ZK_FILTER_ADDED";

    /**
     * 展开左侧
     */
    public static final String LEFT_EXTEND = "LEFT_EXTEND";

    /**
     * 收缩左侧
     */
    public static final String LEFT_COLLAPSE = "LEFT_COLLAPSE";

    /**
     * zk导入开始事件
     */
    public static final String ZK_IMPORT_START = "ZK_IMPORT_START";

    /**
     * zk导入结束事件
     */
    public static final String ZK_IMPORT_FINISH = "ZK_IMPORT_FINISH";

    /**
     * zk搜索开始事件
     */
    public static final String ZK_SEARCH_START = "ZK_SEARCH_START";

    /**
     * zk搜索结束事件
     */
    public static final String ZK_SEARCH_FINISH = "ZK_SEARCH_FINISH";

    /**
     * zk终端打开事件
     */
    public static final String ZK_OPEN_TERMINAL = "ZK_OPEN_TERMINAL";

    /**
     * zk终端关闭事件
     */
    public static final String ZK_CLOSE_TERMINAL = "ZK_CLOSE_TERMINAL";

    /**
     * 添加分组事件
     */
    public static final String ZK_ADD_GROUP = "ZK_ADD_GROUP";

    /**
     * 添加连接事件
     */
    public static final String ZK_ADD_CONNECT = "ZK_ADD_CONNECT";

    /**
     * zk认证
     */
    public static final String ZK_AUTH = "ZK_AUTH";

    /**
     * zk认证列表
     */
    public static final String ZK_AUTH_MAIN = "ZK_AUTH_MAIN";

    /**
     * zk过滤列表
     */
    public static final String ZK_FILTER_MAIN = "ZK_FILTER_MAIN";
}
