package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.fx.plus.event.Event;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKAuthEnabledEvent extends Event<ZKAuth> {

    {
        super.group(ZKEventGroups.AUTH_ACTION);
        super.type( ZKEventTypes.ZK_AUTH_ENABLED);
    }
}
