package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024/4/23
 */
@Setter
@Getter
public class ZKHistoryRestoreEvent extends Event<ZKDataHistory> implements EventFormatter {

    private ZKNodeTreeItem item;

    @Override
    public String eventFormat() {
        return String.format("[%s] 数据已恢复", this.data().getPath());
    }
}
