package cn.oyzh.easyzk.trees;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
import cn.oyzh.fx.plus.trees.RichTreeItemValue;
import javafx.scene.paint.Color;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public abstract class ZKTreeItemValue extends RichTreeItemValue {

    @Override
    public void flushGraphicColor() {
        if (this.graphic() instanceof SVGGlyph glyph) {
            if (ThemeManager.isDarkMode()) {
                glyph.setColor(Color.WHITE);
            } else {
                glyph.setColor(Color.BLACK);
            }
        }
    }
}
