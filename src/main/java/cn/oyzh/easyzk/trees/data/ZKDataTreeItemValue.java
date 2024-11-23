package cn.oyzh.easyzk.trees.data;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKDataTreeItemValue extends ZKTreeItemValue {

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/file-text.svg", 10);
        }
        return super.graphic();
    }

    @Override
    public String name() {
        return I18nHelper.data();
    }
}
