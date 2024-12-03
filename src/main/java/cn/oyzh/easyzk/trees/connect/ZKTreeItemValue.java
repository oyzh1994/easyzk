package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public abstract class ZKTreeItemValue extends RichTreeItemValue {

    public ZKTreeItemValue() {
    }

    public ZKTreeItemValue(RichTreeItem<?> item) {
        super(item);
    }
}
