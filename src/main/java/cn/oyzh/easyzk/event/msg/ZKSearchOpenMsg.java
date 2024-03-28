package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/03/27
 */
@Getter
@Accessors(fluent = true)
public class ZKSearchOpenMsg implements EventMsg {

    private final String name = ZKEventTypes.ZK_SEARCH_OPEN;

    private final String group = ZKEventGroups.SEARCH_ACTION;

    @Setter
    private ZKSearchParam searchParam;
}
