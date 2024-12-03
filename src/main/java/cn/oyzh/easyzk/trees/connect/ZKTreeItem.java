package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import lombok.NonNull;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/2/7
 */
public abstract class ZKTreeItem<V extends ZKTreeItemValue> extends RichTreeItem<V>   {

    public ZKTreeItem(@NonNull RichTreeView treeView) {
        super(treeView);
    }
}
