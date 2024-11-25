package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
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
public class ZKNodeAddedEvent extends Event<String> implements EventFormatter {

    private ZKConnect info;

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nHelper.nodeAdded() + ":%s", this.info.getName(), this.data());
    }

}
