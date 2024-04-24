package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author oyzh
 * @since 2024/4/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ZKHistoryAddEvent extends Event<ZKDataHistory> {

    private ZKNodeTreeItem item;

}
