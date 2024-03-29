package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKConnectionConnectedEvent extends Event<ZKClient> implements EventFormatter {


    {
        super.group(ZKEventGroups.CONNECTION_ACTION);
        super.type(ZKEventTypes.ZK_CONNECTION_CONNECTED);
    }

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已连接", this.data().infoName());
    }

    public ZKInfo info() {
        return this.data().zkInfo();
    }
}
