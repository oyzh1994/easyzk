package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.fx.ZookeeperSVGGlyph;
import cn.oyzh.fx.gui.tree.view.RichTreeItemValue;
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
public class ZKConnectTreeItemValue extends RichTreeItemValue {

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
        if (this.graphic == null) {
            this.graphic = new ZookeeperSVGGlyph(12);
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
