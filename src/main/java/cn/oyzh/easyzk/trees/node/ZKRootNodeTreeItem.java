package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import javafx.scene.control.MenuItem;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKRootNodeTreeItem extends ZKNodeTreeItem {

    public ZKRootNodeTreeItem(@NonNull ZKNode value, ZKNodeTreeView treeView, ZKClient client) {
        super(value, treeView, client);
        // this.setFilterable(false);
    }

    @Override
    public List<MenuItem> getMenuItems() {
        if (this.loading) {
            return super.getMenuItems();
        }
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem add = MenuItemHelper.addNode("12", this::addNode);
        items.add(add);
        FXMenuItem reload = MenuItemHelper.refreshData("12", this::reloadChild);
        items.add(reload);
        if (this.value.parentNode()) {
            FXMenuItem unload = MenuItemHelper.unload("12", this::unloadChild);
            FXMenuItem loadAll = MenuItemHelper.loadAll("12", this::loadChildAll);
            FXMenuItem expandAll = MenuItemHelper.expandAll("12", this::expandAll);
            FXMenuItem collapseAll = MenuItemHelper.collapseAll("12", this::collapseAll);
            items.add(unload);
            items.add(loadAll);
            items.add(expandAll);
            items.add(collapseAll);
        }
        if (this.value.hasReadPerm()) {
            FXMenuItem export = MenuItemHelper.exportData("12", this::exportData);
            items.add(export);
        }
        FXMenuItem auth = MenuItemHelper.authNode("12", this::authNode);
        items.add(auth);
        return items;
    }

    @Override
    public void rename() {
    }

    @Override
    public void delete() {
    }


}
