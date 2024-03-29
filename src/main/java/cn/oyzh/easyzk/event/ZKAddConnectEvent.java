package cn.oyzh.easyzk.event;


import cn.oyzh.fx.plus.event.Event;

/**
 * @author oyzh
 * @since 2024/3/29
 */
public class ZKAddConnectEvent extends Event<Object> {

    {
        super.type(ZKEventTypes.ZK_ADD_CONNECT);
    }
}
