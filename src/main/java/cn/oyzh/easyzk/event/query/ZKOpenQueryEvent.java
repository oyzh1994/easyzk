package cn.oyzh.easyzk.event.query;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024-11-18
 */
public class ZKOpenQueryEvent extends Event<ZKQuery> {
    public ZKClient getClient() {
        return client;
    }

    public void setClient(ZKClient client) {
        this.client = client;
    }

    private ZKClient client;

    public ZKConnect zkConnect() {
        return this.client.zkConnect();
    }
}
