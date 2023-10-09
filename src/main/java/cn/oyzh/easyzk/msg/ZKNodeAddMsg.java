package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.domain.ZKInfo;
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
public class ZKNodeAddMsg implements ZKMsg, ZKMsgFormat {

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
