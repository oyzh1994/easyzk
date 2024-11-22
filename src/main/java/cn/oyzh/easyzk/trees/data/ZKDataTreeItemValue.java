package cn.oyzh.easyzk.trees.data;

import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.i18n.I18nHelper;
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

    public ZKDataTreeItemValue(@NonNull final ZKDataTreeItem item) {
        super(item);
    }

    @Override
    public SVGGlyph graphic() {
        if (this.item().getGraphic() == null ) {
           return new SVGGlyph("/font/file-text.svg", 10);
        }
        return null;
    }

    @Override
    public String name() {
        return I18nHelper.data();
    }
}
