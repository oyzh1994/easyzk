package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.tree.FXTreeCell;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.drag.DragUtil;
import cn.oyzh.fx.plus.drag.DrapNodeHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import lombok.extern.slf4j.Slf4j;

/**
 * zk树节点工厂
 *
 * @author oyzh
 * @since 2023/3/31
 */
@Slf4j
public class ZKTreeCell extends FXTreeCell<BaseTreeItemValue> {

    /**
     * 拖动内容
     */
    public static final String DRAG_CONTENT = "zk_drag";

    /**
     * 拖动处理
     */
    private DrapNodeHandler drapNodeHandler;

    {
        this.setCursor(Cursor.HAND);
    }

    @Override
    public Node initGraphic() {
        TreeItem<?> item = this.getTreeItem();
        ZKTreeView treeView = (ZKTreeView) this.getTreeView();
        if (item instanceof ZKNodeTreeItem treeItem && !treeItem.nodeVisible()) {
            treeView.flushLocal();
            return null;
        }

        // 初始化拖动
        if (item instanceof DragNodeItem dragNodeItem && dragNodeItem.allowDragDrop() && this.drapNodeHandler == null) {
            this.drapNodeHandler = new DrapNodeHandler();
            DragUtil.initDragNode(this.drapNodeHandler, this, DRAG_CONTENT);
        }

        // 刷新图标
        if (item instanceof BaseTreeItem treeItem) {
            treeItem.flushGraphic();
            treeView.flushLocal();
            return treeItem.itemValue();
        }

        return null;
    }
}
