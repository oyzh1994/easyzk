package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKQueryTreeItem extends RichTreeItem<ZKQueryTreeItem.ZKQueryTreeItemValue> {

    private final ZKQuery query;

    public ZKQueryTreeItem(ZKQuery query, RichTreeView treeView) {
        super(treeView);
        this.query = query;
        this.setValue(new ZKQueryTreeItemValue(this));
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
        FXMenuItem openQuery = MenuItemHelper.openQuery("12", this::loadChild);
        items.add(openQuery);
        return items;
    }

    @Override
    public void loadChild() {

    }

    @Override
    public void onPrimaryDoubleClick() {
        this.loadChild();
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKQueryTreeItemValue extends RichTreeItemValue {

        public ZKQueryTreeItemValue(ZKQueryTreeItem item) {
            super(item);
        }

        @Override
        protected ZKQueryTreeItem item() {
            return (ZKQueryTreeItem) super.item();
        }

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new QuerySVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return this.item().query.getName();
        }
    }
}
