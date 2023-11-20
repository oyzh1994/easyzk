package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/21
 */
@Getter
@Accessors(fluent = true)
public class ZKTerminalOpenMsg implements EventMsg {

    private final String name = ZKEventTypes.ZK_OPEN_TERMINAL;

    private final String group = ZKEventGroups.TERMINAL_ACTION;

    @Setter
    private ZKInfo info;

}
