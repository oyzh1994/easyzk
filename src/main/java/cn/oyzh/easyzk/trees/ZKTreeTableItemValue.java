package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.gui.treeTable.RichTreeTableItem;
import cn.oyzh.fx.gui.treeTable.RichTreeTableItemValue;
import cn.oyzh.fx.plus.trees.RichTreeItemValue;
import lombok.NonNull;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public abstract class ZKTreeTableItemValue extends RichTreeTableItemValue {

    public ZKTreeTableItemValue(@NonNull RichTreeTableItem<?> item) {
        super(item);
    }
}
