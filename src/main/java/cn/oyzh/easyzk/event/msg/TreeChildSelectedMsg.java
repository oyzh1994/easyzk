package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.trees.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/29
 */
@Getter
@Accessors(fluent = true)
public class TreeChildSelectedMsg implements EventMsg {

    private final String name = ZKEventTypes.TREE_CHILD_SELECTED;

    private final String group = ZKEventGroups.TREE_ACTION;

    @Setter
    private ZKNodeTreeItem item;
}
