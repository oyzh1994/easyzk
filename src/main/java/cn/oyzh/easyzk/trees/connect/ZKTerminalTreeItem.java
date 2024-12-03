package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKTerminalTreeItem extends ZKTreeItem<ZKTerminalTreeItemValue> {

    public ZKTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKTerminalTreeItemValue());
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
        ZKEventUtil.terminalOpen(this.parent().value());
    }
}
