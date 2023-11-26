package cn.oyzh.easyzk.zk;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.msg.ZKNodeAddedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeDeletedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdatedMsg;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventBuilder;
import cn.oyzh.fx.plus.event.EventUtil;
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
//@Slf4j
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
//                        if (log.isDebugEnabled()) {
                            StaticLog.debug("Update消息已过时.");
//                        }
                        return;
                    }
                }
                if (type == TreeCacheEvent.Type.NODE_ADDED) {
                    long ctime = stat.getCtime();
                    if (nowTime - ctime >= this.maxTimeEffect) {
//                        if (log.isDebugEnabled()) {
                            StaticLog.debug("Add消息已过时.");
//                        }
                        return;
                    }
                }
            }

            switch (type) {
                case NODE_ADDED -> {
                    ZKNodeAddedMsg msg = new ZKNodeAddedMsg();
                    msg.stat(stat);
                    msg.data(nodeData);
                    msg.path(nodePath);
                    msg.client(this.zkClient);
                    Event<?> event1 = EventBuilder.newBuilder().group(msg.group()).type(msg.name()).data(msg).build();
                    EventUtil.fire(event1);
                }
                case NODE_UPDATED -> {
                    ZKNodeUpdatedMsg msg = new ZKNodeUpdatedMsg();
                    msg.stat(stat);
                    msg.data(nodeData);
                    msg.path(nodePath);
                    msg.client(this.zkClient);
                    Event<?> event1 = EventBuilder.newBuilder().group(msg.group()).type(msg.name()).data(msg).build();
                    EventUtil.fire(event1);
                }
                case NODE_REMOVED -> {
                    ZKNodeDeletedMsg msg = new ZKNodeDeletedMsg();
                    msg.stat(stat);
                    msg.path(nodePath);
                    msg.client(this.zkClient);
                    Event<?> event1 = EventBuilder.newBuilder().group(msg.group()).type(msg.name()).data(msg).build();
                    EventUtil.fire(event1);
                }
            }
            String eventType = switch (type) {
                case NODE_ADDED -> ZKEventTypes.ZK_NODE_ADDED;
                case INITIALIZED -> ZKEventTypes.ZK_INITIALIZED;
                case NODE_UPDATED -> ZKEventTypes.ZK_NODE_UPDATED;
                case NODE_REMOVED -> ZKEventTypes.ZK_NODE_DELETED;
                case CONNECTION_LOST -> ZKEventTypes.ZK_CONNECTION_LOST;
                case CONNECTION_SUSPENDED -> ZKEventTypes.ZK_CONNECTION_SUSPENDED;
                case CONNECTION_RECONNECTED -> ZKEventTypes.ZK_CONNECTION_RECONNECTED;
            };


            // ZKNodeMsg nodeMsg = new ZKNodeMsg(this.zkClient, eventType, zkNode);
            // EventUtil.fire(eventType, nodeMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
