package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKQueryStore;
import cn.oyzh.easyzk.zk.ZKClient;
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
 * @since 2024/01/25
 */
public class ZKQueryTypeTreeItem extends RichTreeItem<ZKQueryTypeTreeItemValue> {

    private final ZKQueryStore queryStore = ZKQueryStore.INSTANCE;

    public ZKQueryTypeTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKQueryTypeTreeItemValue());
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    public ZKClient client() {
        return this.parent().client();
    }

    public ZKConnect zkConnect() {
        return this.parent().value();
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addQuery = MenuItemHelper.addQuery("12", this::addQuery);
        items.add(addQuery);
        return items;
    }

    private void addQuery() {
        ZKEventUtil.addQuery(this.parent().client());
    }

    @Override
    public void loadChild() {
        if (!this.isLoading() && !this.isLoaded()) {
            try {
                this.setLoaded(true);
                this.setLoading(true);
                String iid = this.parent().getId();
                List<ZKQuery> queries = this.queryStore.list(iid);
                List<TreeItem<?>> items = new ArrayList<>();
                for (ZKQuery query : queries) {
                    items.add(new ZKQueryTreeItem(query, this.getTreeView()));
                }
                this.setChild(items);
                this.expend();
            } catch (Exception ex) {
                ex.printStackTrace();
                this.setLoaded(false);
            } finally {
                this.setLoading(false);
            }
        }
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoaded()) {
            this.loadChild();
        } else {
            super.onPrimaryDoubleClick();
        }
    }

    public void add(ZKQuery query) {
        this.addChild(new ZKQueryTreeItem(query, this.getTreeView()));
    }
}
