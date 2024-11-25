package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class ZKNodeChangedEvent extends Event<String> {

    private ZKClient client;

    public ZKConnect info() {
        return this.client.zkInfo();
    }

}
