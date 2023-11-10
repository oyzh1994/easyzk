package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.plus.controls.tree.FlexTreeView;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/2/7
 */
@Slf4j
public abstract class ZKTreeItem extends RichTreeItem   {

    /**
     * zk树
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKTreeView treeView;

    @Override
    public ZKTreeItemValue itemValue() {
        return (ZKTreeItemValue) super.getValue();
    }

    @Override
    public void treeView(FlexTreeView treeView) {
        this.treeView = (ZKTreeView) treeView;
    }
}
