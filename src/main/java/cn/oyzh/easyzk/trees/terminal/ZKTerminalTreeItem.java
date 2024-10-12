package cn.oyzh.easyzk.trees.terminal;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.data.ZKDataTreeItemValue;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeView;
import cn.oyzh.fx.plus.trees.RichTreeView;
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

    /**
     * 当前节点的父zk节点
     *
     * @return 父zk节点
     */
    public ZKConnectTreeItem parent() {
        TreeItem parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
        ZKEventUtil.terminalOpen(this.parent().value());
    }
}
