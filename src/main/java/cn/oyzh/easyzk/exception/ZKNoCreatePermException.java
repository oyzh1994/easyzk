package cn.oyzh.easyzk.exception;

/**
 * zk节点无子节点创建权限异常
 *
 * @author oyzh
 * @since 2022/7/8
 */
public class ZKNoCreatePermException extends ZKNoAuthException {

    public ZKNoCreatePermException(String path) {
        super(path);
    }
}
