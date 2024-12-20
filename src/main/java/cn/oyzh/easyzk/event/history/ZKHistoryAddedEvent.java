package cn.oyzh.easyzk.event.history;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.event.Event;
import javafx.scene.control.TreeItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/4/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(fluent = true)
public class ZKHistoryAddedEvent extends Event<ZKDataHistory> {

    private TreeItem<?> item;

}
