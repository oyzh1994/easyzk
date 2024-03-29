package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/19
 */
@Getter
@Accessors(fluent = true)
public class TreeChildFilterMsg extends Event<Object> {

    {
        super.group(ZKEventGroups.TREE_ACTION);
        super.type(ZKEventTypes.TREE_CHILD_FILTER);
    }
}
