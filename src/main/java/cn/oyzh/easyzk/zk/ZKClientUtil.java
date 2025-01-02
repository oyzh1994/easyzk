package cn.oyzh.easyzk.zk;

import cn.oyzh.common.thread.ThreadLocalUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.client.ZKClientActionEvent;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.jute.Record;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.proto.SetDataRequest;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * zk客户端工具类
 *
 * @author oyzh
 * @since 2023/04/25
 */
@UtilityClass
public class ZKClientUtil {

    /**
     * 构建zk客户端
     *
     * @param host                连接地址
     * @param retryPolicy         重试策略
     * @param connectionTimeoutMs 连接超时毫秒值
     * @param sessionTimeoutMs    会话超时毫秒值
     * @return zk客户端
     */
    public static CuratorFramework build(@NonNull String host, @NonNull RetryPolicy retryPolicy, int connectionTimeoutMs,
                                         int sessionTimeoutMs, List<AuthInfo> authInfos, boolean compatibility, String iid, Consumer<ZooKeeper> zooKeeperConsumer) {
        ExecutorService service = Executors.newCachedThreadPool();
        // 构建builder
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(host)
                .maxCloseWaitMs(500)
                .authorization(authInfos)
                .runSafeService(service)
                .retryPolicy(retryPolicy)
                .zk34CompatibilityMode(true)
                .threadFactory(ZKThread::new)
                .waitForShutdownTimeoutMs(500)
                // .zk34CompatibilityMode(compatibility)
                .zookeeperFactory(new ZKFactory(iid, zooKeeperConsumer))
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs);
        return builder.build();
    }

    public static ZKClient newClient(ZKConnect zkConnect) {
        return new ZKClient(zkConnect);
    }
}
