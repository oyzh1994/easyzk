package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.plus.trees.RichTreeItem;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/2/7
 */
public abstract class ZKTreeItem<V extends ZKTreeItemValue> extends RichTreeItem<V>   {

    public ZKTreeItem(ZKTreeView treeView) {
        super(treeView);
    }

    @Override
    public ZKTreeView getTreeView() {
        return (ZKTreeView) super.getTreeView();
    }
}
