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
public class ZKFilterMainMsg implements ZKMsg {

    private final String name = ZKEventTypes.ZK_FILTER_MAIN;

    private final String group = ZKEventGroups.FILTER_ACTION;
}
