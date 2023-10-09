package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.easyzk.domain.ZKInfo;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class ZKConnectTreeItemValue extends BaseTreeItemValue {

    private final ZKConnectTreeItem treeItem;

    public ZKConnectTreeItemValue(@NonNull ZKConnectTreeItem treeItem) {
        this.treeItem = treeItem;
        this.flushGraphic();
        this.flushName();
        treeItem.stateProperty().addListener((observableValue, bytes, t1) -> this.flushGraphicColor());
    }

    @Override
    public String name() {
        return this.treeItem.value().getName();
    }

    @Override
    public boolean flushGraphic() {
        ZKInfo value = treeItem.value();
        SVGGlyph glyph = this.graphic();
        if (glyph == null) {
            glyph = treeItem.value().isCluster() ? new SVGGlyph("/font/cluster.svg", "12") : new SVGGlyph("/font/server-connection.svg", "12");
            this.graphic(glyph);
            return true;
        }
        if (value.isCluster() && !glyph.getUrl().contains("cluster")) {
            glyph = new SVGGlyph("/font/cluster.svg", "12");
            this.graphic(glyph);
            return true;
        }
        if (!value.isCluster() && !glyph.getUrl().contains("connection")) {
            glyph = new SVGGlyph("/font/server-connection.svg", "12");
            this.graphic(glyph);
            return true;
        }
        return false;
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = this.graphic();
        if (this.treeItem.isConnect()) {
            if (glyph.getColor() != Color.GREEN) {
                glyph.setColor(Color.GREEN);
            }
        } else if (glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
        }
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }
}
