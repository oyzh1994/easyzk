package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/4/23
 */
@Setter
@Getter
@Accessors(fluent = true)
public class ZKHistoryRestoreEvent extends Event<byte[]> implements EventFormatter {

    private ZKNodeTreeItem item;

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nResourceBundle.i18nString("base.data", "base.restored"), this.item.nodePath());
    }

    public ZKInfo info(){
        return item.info();
    }
}
