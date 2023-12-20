package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.EventMsg;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKAuthMsg implements EventMsg {

    @Setter
    private String user;

    @Setter
    private String password;

    @Setter
    private boolean result;

    @Setter
    private ZKNodeTreeItem item;

    private final String name = ZKEventTypes.ZK_AUTH;

    private final String group = ZKEventGroups.AUTH_ACTION;
}
