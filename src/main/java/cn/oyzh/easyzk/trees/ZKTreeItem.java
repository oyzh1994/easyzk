package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.plus.controls.tree.FlexTreeView;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeView;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/2/7
 */
@Slf4j
public abstract class ZKTreeItem<V extends ZKTreeItemValue> extends RichTreeItem<V>   {

    public ZKTreeItem(ZKTreeView treeView) {
        super(treeView);
    }

    @Override
    public ZKTreeView getTreeView() {
        return (ZKTreeView) super.getTreeView();
    }
}
