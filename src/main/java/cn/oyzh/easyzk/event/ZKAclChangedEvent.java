package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeView;
import cn.oyzh.fx.plus.event.Event;

/**
 * @author oyzh
 * @since 2024/4/24
 */
public class ZKAclChangedEvent extends Event<ZKNodeTreeItem> {

    public ZKNodeTreeView treeView() {
        return this.data().getTreeView();
    }

}
