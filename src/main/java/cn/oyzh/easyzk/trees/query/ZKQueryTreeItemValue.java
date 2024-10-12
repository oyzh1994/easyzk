package cn.oyzh.easyzk.trees.query;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.QuerySVGGlyph;
import cn.oyzh.fx.plus.controls.svg.TerminalSVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import javafx.scene.Node;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKQueryTreeItemValue extends ZKTreeItemValue {

    public ZKQueryTreeItemValue(){
        this.flushText();
        this.flushGraphic();
    }

    @Override
    public void flushGraphic() {
        Node glyph = this.graphic();
        // 设置图标
        if (glyph == null ) {
            glyph = new QuerySVGGlyph("10");
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return I18nHelper.query();
    }
}
