package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
public class ZKAuthAuthedEvent extends Event<ZKNodeTreeItem> {

    private String user;

    private boolean result;

    private String password;

    {
        super.type( ZKEventTypes.ZK_AUTH);
        super.group(ZKEventGroups.AUTH_ACTION);
    }
}
