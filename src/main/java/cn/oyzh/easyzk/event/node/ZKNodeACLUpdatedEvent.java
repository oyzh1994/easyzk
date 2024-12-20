package cn.oyzh.easyzk.event.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/4/24
 */
public class ZKNodeACLUpdatedEvent extends Event<ZKConnect> implements EventFormatter {

    @Setter
    @Accessors(chain = false, fluent = true)
    private String nodePath;

    @Override
    public String eventFormat() {
        return String.format("[%s:%s acl updated, path:%s] ", I18nHelper.connect(), this.data().getName(), this.nodePath);
    }
}
