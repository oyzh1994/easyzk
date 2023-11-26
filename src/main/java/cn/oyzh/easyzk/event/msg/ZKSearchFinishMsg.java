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
 * @since 2023/11/11
 */
@Getter
@Accessors(fluent = true)
public class ZKSearchFinishMsg implements EventMsg {

    private final String name = ZKEventTypes.ZK_SEARCH_FINISH;

    private final String group = ZKEventGroups.SEARCH_ACTION;

    @Setter
    private ZKSearchParam searchParam;
}
