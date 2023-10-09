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
public class ZKInfoAddedMsg implements ZKMsg, ZKMsgFormat {

    private final String name = ZKEventTypes.ZK_INFO_ADDED;

    private final String group = ZKEventGroups.INFO_ACTION;

    @Setter
    private ZKInfo info;

    public String formatMsg() {
        return String.format("连接[%s] 已新增", this.info.getName());
    }
}
