package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKServerTreeItem extends RichTreeItem<ZKServerTreeItemValue> {

    public ZKServerTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKServerTreeItemValue());
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openServer = MenuItemHelper.openServer("12", this::loadChild);
        items.add(openServer);
        return items;
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    public ZKConnect zkConnect() {
        return this.parent().value();
    }

    @Override
    public void loadChild() {
        ZKEventUtil.server(this.parent().client());
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.loadChild();
    }

}
