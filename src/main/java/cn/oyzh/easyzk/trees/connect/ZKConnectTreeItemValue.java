package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.experimental.Accessors;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
@Accessors(chain = true, fluent = true)
public class ZKConnectTreeItemValue extends ZKTreeItemValue {

    public ZKConnectTreeItemValue(@NonNull ZKConnectTreeItem item) {
        super(item);
    }

    @Override
    protected ZKConnectTreeItem item() {
        return (ZKConnectTreeItem) super.item();
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.item().getGraphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/Zookeeper1.svg", 12);
            glyph.disableTheme();
            return glyph;
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        ZKConnectTreeItem item = this.item();
        if (item.isConnected() || item.isConnecting()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }
}
