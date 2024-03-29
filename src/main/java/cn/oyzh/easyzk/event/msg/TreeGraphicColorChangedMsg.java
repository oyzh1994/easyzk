package cn.oyzh.easyzk.event.msg;

import cn.oyzh.easyzk.event.ZKEventGroups;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.fx.plus.event.Event;
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
public class TreeGraphicColorChangedMsg extends Event<TreeItem<?>> {

    {
        super.group(ZKEventGroups.TREE_ACTION);
        super.type(ZKEventTypes.TREE_GRAPHIC_COLOR_CHANGED);
    }
}
