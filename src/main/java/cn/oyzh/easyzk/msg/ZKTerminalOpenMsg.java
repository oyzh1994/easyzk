package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/21
 */
@Getter
@Accessors(fluent = true)
public class ZKTerminalOpenMsg implements ZKMsg {

    private final String name = ZKEventTypes.ZK_OPEN_TERMINAL;

    private final String group = ZKEventGroups.TERMINAL_ACTION;

    @Setter
    private ZKInfo info;

}
