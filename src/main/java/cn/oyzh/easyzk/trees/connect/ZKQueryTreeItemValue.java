package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKQueryTreeItemValue extends RichTreeItemValue {

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
        return this.item().value.getName();
    }
}
