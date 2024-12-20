package cn.oyzh.easyzk.zk;

import org.apache.curator.utils.ZookeeperFactory;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;

/**
 * @author oyzh
 * @since 2023/9/27
 */
public class ZKFactory implements ZookeeperFactory {

    @Override
    public ZooKeeper newZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly) throws Exception {
        System.setProperty("java.security.auth.login.config", "D:\\Workspaces\\OYZH\\easyzk\\docker\\sasl\\jaas_client.conf");
        // System.setProperty("java.security.auth.login.config", "D:\\Workspaces\\OYZH\\easyzk\\docker\\sasl\\jaas2_client.conf");
        // ZKClientConfig clientConfig = new ZKClientConfig("D:\\Workspaces\\OYZH\\easyzk\\docker\\sasl\\jaas2.conf");
        ZKClientConfig clientConfig = new ZKClientConfig();
        // ZooKeeperSaslClient
        if (connectString.contains("12191")) {
            clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_1");
        } else if (connectString.contains("12192")) {
            clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_2");
        } else if (connectString.contains("12193")) {
            clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_3");
        }
        // clientConfig.setProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, "test:123456");
        // clientConfig.setProperty(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, ClientCnxnSocketNetty.class.getName());
        return new ZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly, clientConfig);
    }
}
