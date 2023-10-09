package cn.oyzh.easyzk.exception;

import lombok.Getter;
import org.apache.zookeeper.KeeperException;

/**
 * zk无权限异常
 *
 * @author oyzh
 * @since 2023/5/31
 */
public class ZKNoAuthException extends KeeperException.NoAuthException {

    @Getter
    protected String path;

    public ZKNoAuthException(String path) {
        this.path = path;
    }

}
