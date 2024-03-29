package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
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
public class ZKInfoAddedEvent extends Event<ZKInfo> implements EventFormatter {

    {
        super.group(ZKEventGroups.INFO_ACTION);
        super.type(ZKEventTypes.ZK_INFO_ADDED);
    }

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已新增", this.data().getName());
    }
}
