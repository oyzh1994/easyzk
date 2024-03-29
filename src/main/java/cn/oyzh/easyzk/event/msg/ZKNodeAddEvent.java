package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
public class ZKNodeAddEvent extends Event<String> implements EventFormatter {

    {
        super.group(ZKEventGroups.NODE_ACTION);
        super.type(ZKEventTypes.ZK_NODE_ADD);
    }

    private ZKInfo info;

    @Override
    public String eventFormat() {
        return String.format("[%s] 新增节点:%s", this.info.getName(), this.data());
    }

}
