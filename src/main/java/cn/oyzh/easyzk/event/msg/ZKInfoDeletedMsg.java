package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKInfoDeletedMsg extends Event<ZKInfo> implements EventFormatter {

    {
        super.group(ZKEventGroups.INFO_ACTION);
        super.type(ZKEventTypes.ZK_INFO_DELETED);
    }

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已删除", this.data().getName());
    }
}
