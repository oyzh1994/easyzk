package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.plus.trees.RichTreeCell;
import lombok.extern.slf4j.Slf4j;

/**
 * zk树节点工厂
 *
 * @author oyzh
 * @since 2023/3/31
 */
@Slf4j
public class ZKTreeCell extends RichTreeCell<ZKTreeItemValue> {

    // @Override
    // public Node initGraphic() {
    //     TreeItem<?> item = this.getTreeItem();
    //     ZKTreeView treeView = (ZKTreeView) this.getTreeView();
    //     // if (item instanceof ZKNodeTreeItem treeItem && !treeItem.nodeVisible()) {
    //     //     treeView.flushLocal();
    //     //     return null;
    //     // }
    //     return super.initGraphic();
    // }
}
