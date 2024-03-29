package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/29
 */
@Getter
@Accessors(fluent = true)
public class TreeChildSelectedEvent extends Event<ZKNodeTreeItem> {

    {
        super.group(ZKEventGroups.TREE_ACTION);
        super.type(ZKEventTypes.TREE_CHILD_SELECTED);
    }

}
