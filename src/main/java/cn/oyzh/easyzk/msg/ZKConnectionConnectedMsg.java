package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.zk.ZKClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKConnectionConnectedMsg implements ZKMsg, ZKMsgFormat {

    private final String name = ZKEventTypes.ZK_CONNECTION_CONNECTED;

    private final String group = ZKEventGroups.CONNECTION_ACTION;

    @Setter
    private ZKClient client;

    @Override
    public String formatMsg() {
        return String.format("[%s] 客户端已连接", this.client.infoName());
    }

    public ZKInfo info() {
        return this.client.zkInfo();
    }
}
