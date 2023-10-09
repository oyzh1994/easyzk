package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKNodeDeleteMsg implements ZKMsg, ZKMsgFormat {

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
