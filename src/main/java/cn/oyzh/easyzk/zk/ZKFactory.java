package cn.oyzh.easyzk.zk;

import cn.oyzh.common.log.JulLog;
import org.apache.curator.utils.ZookeeperFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;

/**
 * @author oyzh
 * @since 2023/9/27
 */
public class ZKFactory implements ZookeeperFactory {

    /**
     * zk连接id
     */
    private final String iid;

    public ZKFactory(String iid) {
        this.iid = iid;
    }

    @Override
    public ZooKeeper newZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly) throws Exception {
        ZKClientConfig clientConfig = new ZKClientConfig();
        // 判断是否开始sasl配置
        if (ZKSASLUtil.isNeedSasl(this.iid)) {
            JulLog.info("连接:{} 执行sasl认证", this.iid);
            clientConfig.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, "true");
            clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, this.iid);
        } else {// 重置sasl配置
            JulLog.debug("连接:{} 无需sasl认证", this.iid);
            clientConfig.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, ZKClientConfig.ENABLE_CLIENT_SASL_DEFAULT);
            clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, ZKClientConfig.LOGIN_CONTEXT_NAME_KEY_DEFAULT);
        }
        // clientConfig.setProperty(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, ClientCnxnSocketNetty.class.getName());
        return new ZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly, clientConfig);
    }
}
