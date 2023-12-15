package cn.oyzh.easyzk.zk;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.RetryOneTime;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * zk客户端工具类
 *
 * @author oyzh
 * @since 2023/04/25
 */
//@Slf4j
@UtilityClass
public class ZKClientUtil {

    // /**
    //  * 构建zk客户端
    //  *
    //  * @param zkInfo      zk信息
    //  * @param retryPolicy 重试策略
    //  * @return zk客户端
    //  */
    // public static CuratorFramework buildClient(@NonNull ZKInfo zkInfo, RetryPolicy retryPolicy) {
    //     if (retryPolicy == null) {
    //         retryPolicy = new RetryOneTime(3_000);
    //     }
    //     // 认证信息列表
    //     List<AuthInfo> authInfos = null;
    //     // 开启自动认证
    //     if (ZKSettingStore.SETTING.isAutoAuth()) {
    //         // 加载已启用的认证
    //         List<ZKAuth> auths = ZKAuthUtil.loadEnableAuths();
    //         authInfos = ZKAuthUtil.toAuthInfo(auths);
    //         StaticLog.info("auto authorization, auths: {}.", auths);
    //     }
    //     return buildClient(zkInfo.getHost(), retryPolicy, zkInfo.connectTimeOutMs(), zkInfo.sessionTimeOutMs(), authInfos);
    // }

    /**
     * 构建zk客户端
     *
     * @param host                连接地址
     * @param retryPolicy         重试策略
     * @param connectionTimeoutMs 连接超时毫秒值
     * @param sessionTimeoutMs    会话超时毫秒值
     * @return zk客户端
     */
    public static CuratorFramework buildClient(@NonNull String host, @NonNull RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs, List<AuthInfo> authInfos) {
        ExecutorService service = Executors.newCachedThreadPool();
        // 构建builder
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(host)
                .maxCloseWaitMs(500)
                .authorization(authInfos)
                // .waitForShutdownTimeoutMs(500)
                .runSafeService(service)
                .retryPolicy(retryPolicy)
                .threadFactory(ZKThread::new)
                .zk34CompatibilityMode(true)
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
}
