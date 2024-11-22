package cn.oyzh.easyzk.trees.root;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKRootTreeItemValue extends ZKTreeItemValue {

    @Override
    public String name() {
        return I18nHelper.zk();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic == null) {
            this.graphic = new SVGGlyph("/font/Zookeeper1.svg", 12);
        }
        return super.graphic();
    }
}
