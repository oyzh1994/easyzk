package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/19
 */
@Getter
@Accessors(fluent = true)
public class ZKAuthMainMsg implements ZKMsg {

    private final String name = ZKEventTypes.ZK_AUTH_MAIN;

    private final String group = ZKEventGroups.AUTH_ACTION;
}
