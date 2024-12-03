package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKQueryTreeItem extends ZKTreeItem<ZKQueryTreeItemValue> {

    public ZKQueryTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKQueryTreeItemValue());
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
    }
}
