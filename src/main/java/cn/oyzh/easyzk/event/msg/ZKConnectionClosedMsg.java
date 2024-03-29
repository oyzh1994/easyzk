package cn.oyzh.easyzk.event.msg;

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
public class ZKConnectionClosedMsg extends Event<ZKClient> implements EventFormatter {

    {
        super.group(ZKEventGroups.CONNECTION_ACTION);
        super.type(ZKEventTypes.ZK_CONNECTION_CLOSED);
    }

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已断开", this.data().infoName());
    }

    public ZKInfo info() {
        return this.data().zkInfo();
    }


}
