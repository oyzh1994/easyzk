package cn.oyzh.easyzk.event;

import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
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
public class ZKNodeUpdatedEvent extends Event<String> implements EventFormatter {

    private String infoName;

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nHelper.nodeUpdated() + ":%s", this.infoName, this.data());
    }
}
