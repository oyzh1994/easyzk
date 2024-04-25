package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class ZKNodeAddEvent extends Event<String> implements EventFormatter {

    private ZKInfo info;

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nResourceBundle.i18nString("base.node", "base.add") + ":%s", this.info.getName(), this.data());
    }

}
