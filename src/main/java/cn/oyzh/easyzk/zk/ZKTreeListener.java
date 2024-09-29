package cn.oyzh.easyzk.zk;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.common.log.JulLog;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.zookeeper.data.Stat;

/**
 * zk节点事件监听器
 *
 * @author oyzh
 * @since 2020/4/17
 */
@Accessors(fluent = true, chain = true)
public class ZKTreeListener implements TreeCacheListener {

    /**
     * 监听路径
     */
    @Getter
    private final String path;

    /**
     * zk客户端
     */
    @Getter
    private final ZKClient zkClient;

    /**
     * 消息有效期
     */
    @Getter
    @Setter
    private int maxTimeEffect = 10 * 1000;

    public ZKTreeListener(@NonNull ZKClient zkClient) {
        this("/", zkClient);
    }

    public ZKTreeListener(@NonNull String path, @NonNull ZKClient zkClient) {
        this.path = path;
        this.zkClient = zkClient;
    }

    @Override
    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) {
        try {
            // 获取数据
            TreeCacheEvent.Type type = event.getType();
            ChildData data = event.getData();
            Stat stat = null;
            byte[] nodeData = null;
            String nodePath = null;
            if (data != null) {
                stat = data.getStat();
                nodePath = data.getPath();
                nodeData = data.getData();
            }
            if (stat != null) {
                long nowTime = System.currentTimeMillis();
                if (type == TreeCacheEvent.Type.NODE_UPDATED) {
                    long mtime = stat.getMtime();
                    if (nowTime - mtime >= this.maxTimeEffect) {
                        JulLog.debug("Update消息已过时.");
                        return;
                    }
                }
                if (type == TreeCacheEvent.Type.NODE_ADDED) {
                    long ctime = stat.getCtime();
                    if (nowTime - ctime >= this.maxTimeEffect) {
                        JulLog.debug("Add消息已过时.");
                        return;
                    }
                }
            }

            switch (type) {
                case NODE_ADDED -> ZKEventUtil.nodeAdded(this.zkClient, stat, nodeData, nodePath);
                case NODE_UPDATED -> ZKEventUtil.nodeUpdated(this.zkClient, stat, nodeData, nodePath);
                case NODE_REMOVED -> ZKEventUtil.nodeDeleted(this.zkClient, stat, nodePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
