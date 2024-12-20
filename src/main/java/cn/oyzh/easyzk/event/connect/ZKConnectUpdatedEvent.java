package cn.oyzh.easyzk.event.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKConnectUpdatedEvent extends Event<ZKConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s:%s updated] ", I18nHelper.connect(), this.data().getName());
    }
}
