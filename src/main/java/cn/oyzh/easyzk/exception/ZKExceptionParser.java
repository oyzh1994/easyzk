package cn.oyzh.easyzk.exception;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.ssh.SSHException;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
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

        if (e instanceof RuntimeException) {
            if (e.getCause() != null) {
                e = e.getCause();
            }
        }

        if (e instanceof SSHException e1) {
            if (StrUtil.contains(e.getMessage(), "Auth fail")) {
                return "ssh认证失败，请检查ssh用户名、密码是否正确";
            }
            return e1.getMessage();
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
            return I18nResourceBundle.i18nString("base.actionTimeout");
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
            return I18nResourceBundle.i18nString("base.badArg");
        }

        if (e instanceof KeeperException.BadVersionException) {
            return I18nResourceBundle.i18nString("base.badVersion");
        }

        if (e instanceof KeeperException.UnimplementedException) {
            return I18nResourceBundle.i18nString("base.unimplemented");
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
            return I18nResourceBundle.i18nString("base.systemError");
        }

        if (e instanceof KeeperException.SessionExpiredException) {
            return "会话已超时，请重新连接！";
        }

        if (e instanceof KeeperException.SessionMovedException) {
            return "会话被移除，请重新连接！";
        }

        if (e instanceof KeeperException.ConnectionLossException) {
            return I18nResourceBundle.i18nString("base.connectionLoss");
        }

        String message = e.getMessage();
        if (e instanceof UnsupportedOperationException) {
            return message;
        }

        if (e instanceof IllegalArgumentException) {
            return message;
        }

        if (e instanceof ZKException) {
            return message;
        }

        e.printStackTrace();
        return message;
    }
}
