package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class ZKGroupTreeItemValue extends ZKTreeItemValue {

    private final ZKGroupTreeItem treeItem;

    public ZKGroupTreeItemValue(@NonNull ZKGroupTreeItem treeItem) {
        this.treeItem = treeItem;
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return this.treeItem.value().getName();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.graphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/group.svg", "12");
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        if (this.graphic() instanceof SVGGlyph glyph) {
            if (this.treeItem.isChildEmpty() && glyph.getColor() != Color.BLACK) {
                glyph.setColor(Color.BLACK);
            } else if (!this.treeItem.isChildEmpty() && glyph.getColor() != Color.DEEPSKYBLUE) {
                glyph.setColor(Color.DEEPSKYBLUE);
            }
        }
    }
}
