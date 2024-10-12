package cn.oyzh.easyzk.trees.data;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.NonNull;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKDataTreeItemValue extends ZKTreeItemValue {

    public ZKDataTreeItemValue(){
        this.flushText();
        this.flushGraphic();
    }

    @Override
    public void flushGraphic() {
        Node glyph = this.graphic();
        // 设置图标
        if (glyph == null ) {
            glyph = new SVGGlyph("/font/file-text.svg", 10);
            this.graphic(glyph);
        }
    }

    @Override
    public String name() {
        return I18nHelper.data();
    }
}
