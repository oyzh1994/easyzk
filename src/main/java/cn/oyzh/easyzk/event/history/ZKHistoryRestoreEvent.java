package cn.oyzh.easyzk.event.history;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
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
        return String.format("[%s:%s path:%s restored data] ", I18nHelper.connect(), this.connect().getName(), this.item.nodePath());
    }

    public ZKConnect connect() {
        return this.item.connect();
    }
}
