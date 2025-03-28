package cn.oyzh.easyzk.event.window;

import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2025-02-20
 */
public class ZKShowAddNodeEvent extends Event<ZKNodeTreeItem> {
    public ZKClient getClient() {
        return client;
    }

    public void setClient(ZKClient client) {
        this.client = client;
    }

    private ZKClient client;
}
