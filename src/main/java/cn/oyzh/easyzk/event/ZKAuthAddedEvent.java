package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKAuthAddedEvent extends Event<ZKAuth> {

    {
        super.group(ZKEventGroups.AUTH_ACTION);
        super.type( ZKEventTypes.ZK_AUTH_ADDED);
    }
}
