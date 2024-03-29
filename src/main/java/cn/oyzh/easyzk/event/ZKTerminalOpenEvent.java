package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/21
 */
@Getter
@Accessors(fluent = true)
public class ZKTerminalOpenEvent extends Event<ZKInfo> {

    {
        super.type(ZKEventTypes.ZK_OPEN_TERMINAL);
        super.group(ZKEventGroups.TERMINAL_ACTION);
    }
}
