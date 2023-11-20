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
public class ZKNodeAddMsg implements EventMsg, EventMsgFormatter {

    private final String name = ZKEventTypes.ZK_NODE_ADD;

    private final String group = ZKEventGroups.NODE_ACTION;

    @Setter
    private String path;

    @Setter
    private ZKInfo info;

    @Override
    public String formatMsg() {
        return String.format("[%s] 新增节点[%s]", this.info.getName(), this.path);
    }

}
