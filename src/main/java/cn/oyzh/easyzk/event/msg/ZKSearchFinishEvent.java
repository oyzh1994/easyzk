package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/11
 */
@Getter
@Accessors(fluent = true)
public class ZKSearchFinishEvent extends Event<ZKSearchParam> {

    {
        super.group(ZKEventGroups.SEARCH_ACTION);
        super.type(ZKEventTypes.ZK_SEARCH_FINISH);
    }

}
