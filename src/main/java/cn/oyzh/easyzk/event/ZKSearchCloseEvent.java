package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.fx.plus.event.Event;

/**
 * @author oyzh
 * @since 2024/03/27
 */
public class ZKSearchCloseEvent extends Event<ZKSearchParam> {

    {
        super.group(ZKEventGroups.SEARCH_ACTION);
        super.type(ZKEventTypes.ZK_SEARCH_CLOSE);
    }

}
