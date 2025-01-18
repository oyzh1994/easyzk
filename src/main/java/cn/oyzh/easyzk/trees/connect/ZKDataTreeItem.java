package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.gui.menu.MenuItemHelper;
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
public class ZKDataTreeItem extends RichTreeItem<ZKDataTreeItem.ZKDataTreeItemValue> {

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
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem openData = MenuItemHelper.openData("12", this::open);
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

    private void open() {
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
        this.open();
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKDataTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/file-text.svg", 10);
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.data();
        }
    }
}
