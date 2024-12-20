package cn.oyzh.easyzk.event.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2024/4/24
 */
public class ZKNodeACLUpdatedEvent extends Event<ZKConnect> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s:%s acl updated, path:%s] ", I18nHelper.connect(), this.data().getName(), this.data());
    }
}
