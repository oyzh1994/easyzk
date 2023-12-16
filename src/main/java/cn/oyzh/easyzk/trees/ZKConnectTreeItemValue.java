package cn.oyzh.easyzk.trees;

import cn.oyzh.easyzk.domain.ZKInfo;
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
//@Slf4j
@Accessors(chain = true, fluent = true)
public class ZKConnectTreeItemValue extends ZKTreeItemValue {

    private final ZKConnectTreeItem treeItem;

    public ZKConnectTreeItemValue(@NonNull ZKConnectTreeItem treeItem) {
        this.treeItem = treeItem;
        this.flushGraphic();
        this.flushText();
        treeItem.stateProperty().addListener((observableValue, bytes, t1) -> this.flushGraphicColor());
    }

    @Override
    public String name() {
        return this.treeItem.value().getName();
    }

    @Override
    public void flushGraphic() {
        // 获取树项的值
        ZKInfo value = treeItem.value();
        // 获取图形
        SVGGlyph glyph = this.graphic();
        // 如果图形为空
        if (glyph == null) {
            // 如果值是集群，则使用"/font/cluster.svg"作为URL，大小为"12"
            // 如果值不是集群，则使用"/font/server-connection.svg"作为URL，大小为"12"
            glyph = value.isCluster() ? new SVGGlyph("/font/cluster.svg", "12") : new SVGGlyph("/font/server-connection.svg", "12");
            // 设置图形
            this.graphic(glyph);
        } else if (value.isCluster() && !glyph.getUrl().contains("cluster")) { // 如果值是集群且图形URL不包含"cluster"
            // 使用"/font/cluster.svg"作为URL，大小为"12"
            glyph = new SVGGlyph("/font/cluster.svg", "12");
            // 设置图形
            this.graphic(glyph);
        } else if (!value.isCluster() && !glyph.getUrl().contains("connection")) {// 如果值不是集群且图形URL不包含"connection"
            // 使用"/font/server-connection.svg"作为URL，大小为"12"
            glyph = new SVGGlyph("/font/server-connection.svg", "12");
            // 设置图形
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        // 获取当前图形符号
        SVGGlyph glyph = this.graphic();
        // 如果当前树项已连接，但是图形符号的颜色不是绿色
        if (this.treeItem.isConnected() && glyph.getColor() != Color.GREEN) {
            // 将图形符号的颜色设置为绿色
            glyph.setColor(Color.GREEN);
        } else if (!this.treeItem.isConnected() &&glyph.getColor() != Color.BLACK) {// 如果当前树项未连接，但是图形符号的颜色不是黑色
            // 将图形符号的颜色设置为黑色
            glyph.setColor(Color.BLACK);
        }
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }
}
