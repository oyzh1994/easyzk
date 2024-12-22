package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.fx.gui.svg.glyph.MoreSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKMoreTreeItem extends RichTreeItem<ZKMoreTreeItem.ZKMoreTreeItemValue> {

    public ZKMoreTreeItem(ZKNodeTreeView treeView) {
        super(treeView);
        super.setSortable(false);
        super.setFilterable(false);
        this.setValue(new ZKMoreTreeItemValue());
    }

    @Override
    public ZKNodeTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKNodeTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
        if (!this.isLoading()) {
            ZKNodeTreeItem treeItem = this.parent();
            if (treeItem != null) {
                treeItem.loadChildAsync();
            }
        }
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKMoreTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new MoreSVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.loadMore();
        }
    }
}
