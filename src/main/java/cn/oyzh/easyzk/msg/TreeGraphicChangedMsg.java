package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/28
 */
@Getter
@Accessors(fluent = true)
public class TreeGraphicChangedMsg implements ZKMsg {

    private final String name = ZKEventTypes.TREE_GRAPHIC_CHANGED;

    private final String group = ZKEventGroups.TREE_ACTION;

    @Setter
    private TreeItem<?> item;

}
