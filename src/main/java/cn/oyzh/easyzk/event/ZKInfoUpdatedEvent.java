package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKInfoUpdatedEvent extends Event<ZKInfo> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] "+ I18nHelper.connectionUpdated(), this.data().getName());
    }
}
