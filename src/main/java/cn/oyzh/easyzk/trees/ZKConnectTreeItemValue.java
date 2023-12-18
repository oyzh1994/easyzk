package cn.oyzh.easyzk.trees;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
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
            glyph = value.isCluster() ? new SVGGlyph("/font/cluster.svg", "10") : new SVGGlyph("/font/server-connection.svg", "12");
            // 设置图形
            this.graphic(glyph);
        } else if (value.isCluster() && !glyph.getUrl().contains("cluster")) {
            glyph = new SVGGlyph("/font/cluster.svg", "10");
            // 设置图形
            this.graphic(glyph);
        } else if (!value.isCluster() && !glyph.getUrl().contains("connection")) {
            glyph = new SVGGlyph("/font/server-connection.svg", "10");
            // 设置图形
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        // 获取当前图形符号
        SVGGlyph glyph = this.graphic();
        if (this.treeItem.isConnected()) {
            glyph.setColor(Color.GREEN);
        } else if (!this.treeItem.isConnected()) {
            if (ThemeManager.isDarkMode()) {
                glyph.setColor(Color.WHITE);
            } else {
                glyph.setColor(Color.BLACK);
            }
        }
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }
}
