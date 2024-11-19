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
        ZKClientConfig clientConfig = new ZKClientConfig();
        // clientConfig.setProperty(ZKClientConfig.ZOOKEEPER_CLIENT_CNXN_SOCKET, ClientCnxnSocketNetty.class.getName());
        return new ZooKeeper(connectString, sessionTimeout, watcher, canBeReadOnly, clientConfig);
    }
}
