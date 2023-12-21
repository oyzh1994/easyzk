package cn.oyzh.easyzk.trees.root;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKRootTreeItemValue extends ZKTreeItemValue {

    public ZKRootTreeItemValue() {
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return "Zookeeper连接列表";
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/Zookeeper1.svg", 12);
            this.graphic(glyph);
        }
    }
}
