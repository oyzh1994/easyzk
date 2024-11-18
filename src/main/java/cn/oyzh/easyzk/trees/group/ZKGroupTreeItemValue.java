package cn.oyzh.easyzk.trees.group;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
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

    private final ZKGroupTreeItem item;

    public ZKGroupTreeItemValue(@NonNull ZKGroupTreeItem item) {
        this.item = item;
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return this.item.value().getName();
    }

    @Override
    public void flushGraphic() {
        GroupSVGGlyph glyph = (GroupSVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new GroupSVGGlyph("10");
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        if (this.item.isChildEmpty()) {
            super.flushGraphicColor();
        } else if (this.graphic() instanceof SVGGlyph glyph) {
            glyph.setColor(Color.DEEPSKYBLUE);
        }
    }
}
