package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
import cn.oyzh.fx.plus.event.EventMsgFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKInfoDeletedMsg implements EventMsg, EventMsgFormatter {

    private final String name = ZKEventTypes.ZK_INFO_DELETED;

    private final String group = ZKEventGroups.INFO_ACTION;

    @Setter
    private ZKInfo info;

    public String formatMsg() {
        return String.format("连接[%s] 已删除", this.info.getName());
    }
}
