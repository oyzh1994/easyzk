package cn.oyzh.easyzk.trees.group;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.fx.gui.svg.glyph.GroupSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.NonNull;


/**
 * zk树group值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKGroupTreeItemValue extends ZKTreeItemValue {

    public ZKGroupTreeItemValue(@NonNull ZKGroupTreeItem item) {
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
        GroupSVGGlyph glyph = (GroupSVGGlyph) this.item().getGraphic();
        if (glyph == null) {
            glyph = new GroupSVGGlyph("10");
            glyph.disableTheme();
            return glyph;
        }
        return null;
    }

    @Override
    public Color graphicColor() {
        if (this.item().isChildEmpty()) {
           return super.graphicColor();
        }
        return Color.DEEPSKYBLUE;
    }
}
