package cn.oyzh.easyzk.event.node;

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

    private ZKConnect zkConnect;

    @Override
    public String eventFormat() {
        return String.format("[%s:%s added, path:%s] ", I18nHelper.connect(), this.zkConnect.getName(), this.data());
    }

}
