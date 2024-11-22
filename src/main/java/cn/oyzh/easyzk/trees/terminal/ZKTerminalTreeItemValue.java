package cn.oyzh.easyzk.trees.terminal;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Node;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKTerminalTreeItemValue extends ZKTreeItemValue {

    public ZKTerminalTreeItemValue(ZKTerminalTreeItem item) {
        super(item);
    }

    @Override
    public SVGGlyph graphic() {
        SVGGlyph glyph = (SVGGlyph) this.item().getGraphic();
        // 设置图标
        if (glyph == null) {
            glyph = new TerminalSVGGlyph("10");
            return glyph;
        }
        return null;
    }

    @Override
    public String name() {
        return I18nHelper.terminal();
    }
}
