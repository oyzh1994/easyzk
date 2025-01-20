package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKQueryStore;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKQueryTypeTreeItem extends RichTreeItem<ZKQueryTypeTreeItem.ZKQueryTypeTreeItemValue> {

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

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKQueryTypeTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new QuerySVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.query();
        }
    }
}
