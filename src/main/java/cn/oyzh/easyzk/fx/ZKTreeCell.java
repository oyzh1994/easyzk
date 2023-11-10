package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.tree.FXTreeCell;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.drag.DragUtil;
import cn.oyzh.fx.plus.drag.DrapNodeHandler;
import cn.oyzh.fx.plus.trees.RichTreeCell;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import lombok.Getter;
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
