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
public class ZKDataTreeItem extends RichTreeItem<ZKDataTreeItemValue> {

    public ZKDataTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKDataTreeItemValue());
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> treeItem = super.getParent();
        return (ZKConnectTreeItem) treeItem;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>(2);
        FXMenuItem openData = MenuItemHelper.openData("12", this::loadChild);
        items.add(openData);
        return items;
    }

    public ZKConnect connect() {
        return this.parent().value();
    }

    public ZKConnect zkConnect() {
        return this.parent().value();
    }

    private void setOpening(boolean opening) {
        super.bitValue().set(7, opening);
    }

    private boolean isOpening() {
        return super.bitValue().get(7);
    }

    @Override
    public void loadChild() {
        if (!this.isOpening()) {
            this.setOpening(true);
            super.startWaiting(() -> {
                try {
                    ZKEventUtil.connectionOpened(this.parent());
                } finally {
                    this.setOpening(false);
                }
            });
        }
    }

    @Override
    public void onPrimaryDoubleClick() {
        this.loadChild();
    }

}
