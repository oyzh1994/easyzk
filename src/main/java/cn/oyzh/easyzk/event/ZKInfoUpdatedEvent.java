package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKInfoUpdatedEvent extends Event<ZKInfo> implements EventFormatter {

    {
        super.group(ZKEventGroups.INFO_ACTION);
        super.type(ZKEventTypes.ZK_INFO_UPDATED);
    }

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已修改", this.data().getName());
    }
}
