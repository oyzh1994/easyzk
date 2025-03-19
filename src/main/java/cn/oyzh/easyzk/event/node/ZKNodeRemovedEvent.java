package cn.oyzh.easyzk.event.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKNodeRemovedEvent extends Event<String>   {

    public ZKClient getClient() {
        return client;
    }

    public void setClient(ZKClient client) {
        this.client = client;
    }

    private ZKClient client;

    public ZKConnect connect(){
        return this.client.zkConnect();
    }
}
