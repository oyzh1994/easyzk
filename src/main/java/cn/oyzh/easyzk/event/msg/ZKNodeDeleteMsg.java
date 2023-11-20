package cn.oyzh.easyzk.event.msg;

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
public class ZKNodeDeleteMsg implements EventMsg, EventMsgFormatter {

    private final String name = ZKEventTypes.ZK_NODE_DELETE;

    private final String group = ZKEventGroups.NODE_ACTION;

    @Setter
    private String path;

    @Setter
    private String infoName;

    @Setter
    private boolean delChildren;

    @Override
    public String formatMsg() {
        if (this.delChildren) {
            return String.format("[%s] 删除节点[%s]及子节点", this.infoName, this.path);
        }
        return String.format("[%s] 删除节点[%s]", this.infoName, this.path);
    }
}
