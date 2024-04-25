package cn.oyzh.easyzk.event;

import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Setter
@Getter
@Accessors(fluent = true)
public class ZKNodeDeleteEvent extends Event<String> implements EventFormatter {

    private String infoName;

    private boolean delChildren;

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nResourceBundle.i18nString("base.node", "base.delete") + ":%s", this.infoName, this.data());
    }
}
