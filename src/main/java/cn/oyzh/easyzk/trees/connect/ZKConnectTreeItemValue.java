package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
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
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/server-connection.svg", 10);
            glyph.disableTheme();
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
        } else {
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
