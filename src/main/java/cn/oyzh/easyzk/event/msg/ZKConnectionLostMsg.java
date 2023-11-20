package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/19
 */
@Getter
@Accessors(fluent = true)
public class ZKConnectionLostMsg implements EventMsg, EventMsgFormatter {

    private final String name = ZKEventTypes.ZK_CONNECTION_LOST;

    private final String group = ZKEventGroups.CONNECTION_ACTION;

    @Setter
    private ZKClient client;

    @Override
    public String formatMsg() {
        return String.format("[%s] 客户端已中断", this.client.infoName());
    }

    public ZKInfo info() {
        return this.client.zkInfo();
    }
}
