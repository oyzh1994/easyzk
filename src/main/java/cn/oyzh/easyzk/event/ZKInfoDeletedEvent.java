package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKInfoDeletedEvent extends Event<ZKInfo> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("连接[%s] 已删除", this.data().getName());
    }
}
