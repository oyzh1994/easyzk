package cn.oyzh.easyzk.zk;

import org.apache.curator.utils.ZookeeperFactory;
import org.apache.zookeeper.Environment;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.client.ZKClientConfig;

/**
 * @author oyzh
 * @since 2023/9/27
 */
public class ZKFactory implements ZookeeperFactory {

    private final String iid;

    public ZKFactory(String iid) {
        this.iid = iid;
    }

    @Override
    public ZooKeeper newZooKeeper(String connectString, int sessionTimeout, Watcher watcher, boolean canBeReadOnly) throws Exception {
        ZKClientConfig clientConfig = new ZKClientConfig();
        String jaasFile = ZKSASLUtil.getJaasFile();
        if (jaasFile != null) {
            System.setProperty(Environment.JAAS_CONF_KEY, jaasFile);
            clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_" + this.iid.replaceAll("-", ""));
        }
        // System.setProperty("java.security.auth.login.config", "D:\\Workspaces\\OYZH\\easyzk\\docker\\sasl\\jaas_client.conf");
        // // System.setProperty("java.security.auth.login.config", "D:\\Workspaces\\OYZH\\easyzk\\docker\\sasl\\jaas2_client.conf");
        // // ZKClientConfig clientConfig = new ZKClientConfig("D:\\Workspaces\\OYZH\\easyzk\\docker\\sasl\\jaas2.conf");
        // clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_1");
        // // ZooKeeperSaslClient
        // if (connectString.contains("12191")) {
        //     clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_1");
        // } else if (connectString.contains("12192")) {
        //     clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_2");
        // } else if (connectString.contains("12193")) {
        //     clientConfig.setProperty(ZKClientConfig.LOGIN_CONTEXT_NAME_KEY, "Client_3");
        // }
        // clientConfig.setProperty(ZKClientConfig.ZK_SASL_CLIENT_USERNAME, "test:123456");
        // clientConfig.setProperty(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, ClientCnxnSocketNetty.class.getName());
        return new ZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly, clientConfig);
    }
}
