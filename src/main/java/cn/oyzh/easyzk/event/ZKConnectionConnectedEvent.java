package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKConnectionConnectedEvent extends Event<ZKClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] 客户端已连接", this.data().infoName());
    }

    public ZKInfo info() {
        return this.data().zkInfo();
    }
}
