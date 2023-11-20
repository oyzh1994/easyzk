package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.EventMsg;
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
public class TreeGraphicColorChangedMsg implements EventMsg {

    private final String name = ZKEventTypes.TREE_GRAPHIC_COLOR_CHANGED;

    private final String group = ZKEventGroups.TREE_ACTION;

    @Setter
    private TreeItem<?> item;

}
