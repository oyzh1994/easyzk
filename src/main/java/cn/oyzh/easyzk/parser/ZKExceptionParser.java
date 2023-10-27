package cn.oyzh.easyzk.parser;

import cn.oyzh.easyzk.exception.ZKConnectSateException;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.easyzk.exception.ZKNoAdminPermException;
import cn.oyzh.easyzk.exception.ZKNoChildPermException;
import cn.oyzh.easyzk.exception.ZKNoCreatePermException;
import cn.oyzh.easyzk.exception.ZKNoDeletePermException;
import cn.oyzh.easyzk.exception.ZKNoReadPermException;
import cn.oyzh.easyzk.exception.ZKNoWritePermException;
import org.apache.zookeeper.KeeperException;

import java.util.function.Function;

/**
 * zk异常解析器
 *
 * @author oyzh
 * @since 2020/7/2
 */
public class ZKExceptionParser implements Function<Throwable, String> {

    /**
     * 当前实例
     */
    public final static ZKExceptionParser INSTANCE = new ZKExceptionParser();

    @Override
    public String apply(Throwable e) {
        if (e == null) {
            return null;
        }

        if (e instanceof RuntimeException exception) {
            if (exception.getCause() != null) {
                e = e.getCause();
            }
        }

        if (e instanceof ZKNoChildPermException exception) {
            return "[" + exception.getPath() + "]无子节点权限！";
        }

        if (e instanceof ZKNoReadPermException exception) {
            return "[" + exception.getPath() + "]无数据读取权限！";
        }

        if (e instanceof ZKNoWritePermException exception) {
            return "[" + exception.getPath() + "]无数据写入权限！";
        }

        if (e instanceof ZKNoAdminPermException exception) {
            return "[" + exception.getPath() + "]无ACL管理权限！";
        }

        if (e instanceof ZKNoCreatePermException exception) {
            return "[" + exception.getPath() + "]无子节点创建权限！";
        }

        if (e instanceof ZKNoDeletePermException exception) {
            return "[" + exception.getPath() + "]无删除权限！";
        }

        if (e instanceof KeeperException.NoAuthException) {
            return "无操作权限！";
        }

        if (e instanceof KeeperException.NodeExistsException) {
            return "节点已存在！";
        }

        if (e instanceof KeeperException.AuthFailedException) {
            return "节点认证失败！";
        }

        if (e instanceof KeeperException.OperationTimeoutException) {
            return "操作超时！";
        }

        if (e instanceof KeeperException.NoNodeException) {
            return "节点、父节点不存在或已删除！";
        }

        if (e instanceof KeeperException.InvalidACLException) {
            return "无效的ACL！";
        }

        if (e instanceof KeeperException.NotEmptyException) {
            return "节点不为空！";
        }

        if (e instanceof KeeperException.BadArgumentsException) {
            return "参数错误或操作不支持！";
        }

        if (e instanceof KeeperException.BadVersionException) {
            return "版本错误！";
        }

        if (e instanceof KeeperException.UnimplementedException) {
            return "未实现的功能！";
        }

        if (e instanceof KeeperException.DataInconsistencyException) {
            return "数据不一致！";
        }

        if (e instanceof KeeperException.RuntimeInconsistencyException) {
            return "执行不一致！";
        }

        if (e instanceof KeeperException.InvalidCallbackException) {
            return "无效的回调！";
        }

        if (e instanceof KeeperException.APIErrorException) {
            return "API错误！";
        }

        if (e instanceof KeeperException.NotReadOnlyException) {
            return "非只读节点！";
        }

        if (e instanceof KeeperException.MarshallingErrorException) {
            return "队列错误！";
        }

        if (e instanceof KeeperException.NoChildrenForEphemeralsException) {
            return "临时节点不允许创建子节点！";
        }

        if (e instanceof ZKConnectSateException) {
            return "zk连接状态异常！";
        }

        if (e instanceof KeeperException.SystemErrorException) {
            return "系统异常！";
        }

        if (e instanceof KeeperException.SessionExpiredException) {
            return "会话已超时，请重新连接！";
        }

        if (e instanceof KeeperException.SessionMovedException) {
            return "会话被移除，请重新连接！";
        }

        if (e instanceof KeeperException.ConnectionLossException) {
            return "连接丢失！";
        }

        if (e instanceof IllegalArgumentException) {
            return e.getMessage();
        }

        if (e instanceof ZKException) {
            return e.getMessage();
        }

        e.printStackTrace();
        return e.getMessage();
    }
}
