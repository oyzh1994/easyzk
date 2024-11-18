package cn.oyzh.easyzk.trees.terminal;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import javafx.scene.Node;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKTerminalTreeItemValue extends ZKTreeItemValue {

    public ZKTerminalTreeItemValue(){
        this.flushText();
        this.flushGraphic();
    }

    @Override
    public void flushGraphic() {
        Node glyph = this.graphic();
        // 设置图标
        if (glyph == null ) {
            glyph = new TerminalSVGGlyph("10");
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return I18nHelper.terminal();
    }
}
