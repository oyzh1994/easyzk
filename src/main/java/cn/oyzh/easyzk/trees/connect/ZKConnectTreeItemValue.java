package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.beans.value.ChangeListener;
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

    /**
     * 状态监听器
     */
    private ChangeListener<Object> statListener = (obs, oldValue, newValue) -> this.flushGraphicColor();

    public ZKConnectTreeItemValue(@NonNull ZKConnectTreeItem item) {
        this.setProp("_item", item);
        this.flush();
        item.stateProperty().addListener(this.statListener);
    }

    private ZKConnectTreeItem item() {
        return this.getProp("_item");
    }

    @Override
    public String name() {
        return this.item().value().getName();
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            SVGGlyph glyph = new SVGGlyph("/font/Zookeeper1.svg", 12);
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        // 获取当前图形符号
        SVGGlyph glyph = this.graphic();
        if (glyph == null) {
            return;
        }
        ZKConnectTreeItem item = this.item();
        if (item.isConnected() || item.isConnecting()) {
            glyph.setColor(Color.GREEN);
        } else {
            super.flushGraphicColor();
        }
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }

    @Override
    public void destroy() {
        ZKConnectTreeItem item = this.item();
        if (item != null && this.statListener != null) {
            item.stateProperty().removeListener(this.statListener);
            this.statListener = null;
        }
        super.destroy();
    }
}
