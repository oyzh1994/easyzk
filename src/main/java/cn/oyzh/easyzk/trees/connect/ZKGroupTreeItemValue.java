package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.fx.gui.svg.glyph.GroupSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;

/**
 * zk树group值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKGroupTreeItemValue extends RichTreeItemValue {

    public ZKGroupTreeItemValue( ZKGroupTreeItem item) {
        super(item);
    }

    @Override
    protected ZKGroupTreeItem item() {
        return (ZKGroupTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new GroupSVGGlyph("10");
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (this.item().isChildEmpty()) {
            return super.graphicColor();
        }
        return Color.DEEPSKYBLUE;
    }
}
