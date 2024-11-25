package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKConnectOpenedEvent extends Event<ZKConnectTreeItem>  {

    public ZKClient client() {
        return this.data().client();
    }

    public ZKConnect connect() {
        return this.data().client().connect();
    }
}
