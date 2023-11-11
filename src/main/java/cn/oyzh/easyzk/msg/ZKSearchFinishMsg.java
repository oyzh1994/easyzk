package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.dto.ZKSearchParam;
import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/11/11
 */
@Getter
@Accessors(fluent = true)
public class ZKSearchFinishMsg implements ZKMsg {

    private final String name = ZKEventTypes.ZK_SEARCH_FINISH;

    private final String group = ZKEventGroups.SEARCH_ACTION;

    @Setter
    private ZKSearchParam searchParam;
}
