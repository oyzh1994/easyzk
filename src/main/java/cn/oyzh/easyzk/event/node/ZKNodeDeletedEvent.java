package cn.oyzh.easyzk.event.node;

import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
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
public class ZKNodeDeletedEvent extends Event<String> implements EventFormatter {

    private String infoName;

    private boolean delChildren;

    @Override
    public String eventFormat() {
        return String.format("[%s:%s deleted, path:%s] ", I18nHelper.connect(), this.infoName, this.data());
    }
}
