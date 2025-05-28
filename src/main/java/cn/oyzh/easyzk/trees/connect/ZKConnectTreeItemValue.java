package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.fx.svg.glyph.ZookeeperSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.scene.paint.Color;

/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKConnectTreeItemValue extends RichTreeItemValue {

    public ZKConnectTreeItemValue( ZKConnectTreeItem item) {
        super(item);
        super.setRichMode(true);
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
        if (this.graphic == null) {
            this.graphic = new ZookeeperSVGGlyph("12");
        }
        return super.graphic();
    }

    @Override
    public Color graphicColor() {
        if (this.item().isConnected() || this.item().isConnecting()) {
            return Color.GREEN;
        }
        return super.graphicColor();
    }
}
