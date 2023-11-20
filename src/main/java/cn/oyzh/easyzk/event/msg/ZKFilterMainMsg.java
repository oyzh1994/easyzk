package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/19
 */
@Getter
@Accessors(fluent = true)
public class ZKFilterMainMsg implements EventMsg {

    private final String name = ZKEventTypes.ZK_FILTER_MAIN;

    private final String group = ZKEventGroups.FILTER_ACTION;
}
