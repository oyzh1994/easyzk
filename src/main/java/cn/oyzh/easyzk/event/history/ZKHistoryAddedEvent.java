package cn.oyzh.easyzk.event.history;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.event.Event;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2024/4/23
 */
public class ZKHistoryAddedEvent extends Event<ZKDataHistory> {

    private TreeItem<?> item;

    public TreeItem<?> getItem() {
        return item;
    }

    public void setItem(TreeItem<?> item) {
        this.item = item;
    }
}
