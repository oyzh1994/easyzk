package cn.oyzh.easyzk.event.query;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2024-11-18
 */
public class ZKAddQueryEvent extends Event<ZKClient> {

    public ZKConnect zkConnect(){
        return this.data().zkConnect();
    }
}
