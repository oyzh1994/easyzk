package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
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

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> treeItem = super.getParent();
        return (ZKConnectTreeItem) treeItem;
    }

    public ZKConnect connect() {
        return this.parent().value();
    }

    @Override
    public void onPrimaryDoubleClick() {
        super.startWaiting(() -> ZKEventUtil.connectionOpened(this.parent()));
    }
}
