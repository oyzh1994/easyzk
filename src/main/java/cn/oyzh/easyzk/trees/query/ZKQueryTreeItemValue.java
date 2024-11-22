package cn.oyzh.easyzk.trees.query;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.fx.gui.svg.glyph.QuerySVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Node;
import lombok.NonNull;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKQueryTreeItemValue extends ZKTreeItemValue {

    public ZKQueryTreeItemValue(@NonNull ZKQueryTreeItem item) {
        super(item);
    }

    @Override
    public SVGGlyph graphic() {
        SVGGlyph glyph = (SVGGlyph) this.item().getGraphic();
        // 设置图标
        if (glyph == null ) {
            glyph = new QuerySVGGlyph("10");
            return glyph;
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return I18nHelper.query();
    }
}
