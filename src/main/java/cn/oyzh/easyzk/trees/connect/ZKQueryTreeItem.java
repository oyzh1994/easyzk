package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKQueryTreeItem extends RichTreeItem<ZKQueryTreeItem.ZKQueryTreeItemValue> {

    public ZKQueryTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKQueryTreeItemValue());
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
    public void onPrimaryDoubleClick() {
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKQueryTreeItemValue extends RichTreeItemValue {

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
