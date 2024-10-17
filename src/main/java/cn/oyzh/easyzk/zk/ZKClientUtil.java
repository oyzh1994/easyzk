package cn.oyzh.easyzk.zk;

import cn.oyzh.easyzk.domain.ZKInfo;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public static CuratorFramework buildClient(@NonNull String host, @NonNull RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs, List<AuthInfo> authInfos, boolean compatibility) {
        ExecutorService service = Executors.newCachedThreadPool();
        // 构建builder
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(host)
                .maxCloseWaitMs(500)
                .authorization(authInfos)
                .runSafeService(service)
                .retryPolicy(retryPolicy)
                .threadFactory(ZKThread::new)
                .zk34CompatibilityMode(true)
                // .zk34CompatibilityMode(compatibility)
                .zookeeperFactory(new ZKFactory())
                .sessionTimeoutMs(sessionTimeoutMs)
                .connectionTimeoutMs(connectionTimeoutMs);
        return builder.build();
    }

    /**
     * 构建zk监听器
     *
     * @param framework zk客户端
     * @return zk监听器
     */
    public static TreeCache buildTreeCache(@NonNull CuratorFramework framework) {
        return buildTreeCache(framework, "/");
    }

    /**
     * 构建zk监听器
     *
     * @param framework zk客户端
     * @param path      监听路径
     * @return zk监听器
     */
    public static TreeCache buildTreeCache(@NonNull CuratorFramework framework, @NonNull String path) {
        // 构建builder
        TreeCache.Builder builder = TreeCache.newBuilder(framework, path)
                .setCacheData(false)
                .setExecutor(ZKThread::new)
                .setCreateParentNodes(true);
        return builder.build();
    }

    public static ZKClient newClient(ZKInfo zkInfo) {
        return new ZKClient(zkInfo);
    }
}
