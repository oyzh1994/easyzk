package cn.oyzh.easyzk.event.query;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author oyzh
 * @since 2024-11-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ZKOpenQueryEvent extends Event<ZKQuery> {

    private ZKClient client;

    public ZKConnect zkConnect() {
        return this.client.zkConnect();
    }
}
