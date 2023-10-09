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
public class ZKInfoUpdatedMsg implements ZKMsg , ZKMsgFormat{

    @Getter
    private String name = ZKEventTypes.ZK_INFO_UPDATED;

    @Getter
    private String group = ZKEventGroups.INFO_ACTION;

    @Setter
    private ZKInfo info;

    public String formatMsg() {
        return String.format("连接[%s] 已修改", this.info.getName());
    }
}
