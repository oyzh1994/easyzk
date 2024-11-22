package cn.oyzh.easyzk.trees.data;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeView;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKDataTreeItem extends ZKTreeItem<ZKDataTreeItemValue> {

    public ZKDataTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKDataTreeItemValue());
    }

    public ZKConnectTreeItem parent() {
        TreeItem<?> treeItem = super.getParent();
        return (ZKConnectTreeItem) treeItem;
    }

    @Override
    public void onPrimaryDoubleClick() {
        super.startWaiting(() -> ZKEventUtil.connectionOpened(this.parent()));
    }
}
