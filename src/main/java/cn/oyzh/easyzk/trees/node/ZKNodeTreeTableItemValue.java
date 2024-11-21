package cn.oyzh.easyzk.trees.node;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.trees.ZKTreeTableItemValue;
import cn.oyzh.fx.gui.svg.glyph.LockSVGGlyph;
import cn.oyzh.fx.gui.treeTable.RichTreeTableItem;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKNodeTreeTableItemValue extends ZKTreeTableItemValue {

    public ZKNodeTreeTableItemValue(@NonNull ZKNodeTreeTableItem item) {
        super(item);
    }

    @Override
    public ZKNodeTreeTableItem item() {
        return (ZKNodeTreeTableItem) super.item();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = this.graphic();
        if (glyph != null && glyph.isWaiting()) {
            return;
        }
        boolean changed = false;
        if (glyph == null) {
            changed = true;
        } else if (this.item().isNeedAuth() && StringUtil.notEquals(glyph.getProp("_type"), "3")) {
            changed = true;
        } else if (this.item().isEphemeral() && StringUtil.notEquals(glyph.getProp("_type"), "2")) {
            changed = true;
        } else if (StringUtil.notEquals(glyph.getProp("_type"), "1")) {
            changed = true;
        }
        if (changed) {
            if (this.item().isNeedAuth()) {
                glyph = new LockSVGGlyph("11");
                glyph.setProp("_type", "3");
            } else if (this.item().isEphemeral()) {
                glyph = new SVGGlyph("/font/temp.svg", 11);
                glyph.setProp("_type", "2");
            } else {
                glyph = new SVGGlyph("/font/file-text.svg", 11);
                glyph.setProp("_type", "1");
            }
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flushColumn() {
        if (this.pathProperty == null) {
            this.pathProperty = new SimpleStringProperty(this.item().decodeNodeName());
        }
        String extra;
        Integer totalNum = this.item().getNumChildren();
        Integer showNum = this.item().getChildrenSize();
        if (totalNum == null || totalNum == 0) {
            extra = "";
        } else if (showNum.intValue() == totalNum.intValue()) {
            extra = "" + totalNum;
        } else {
            extra = showNum + "/" + totalNum;
        }
        if (this.extraProperty == null) {
            this.extraProperty = new SimpleStringProperty(extra);
        } else {
            this.extraProperty.set(extra);
        }
    }

    /**
     * 刷新图形颜色
     */
    @Override
    public void flushGraphicColor() {
        // 获取图形
        SVGGlyph glyph = this.graphic();
        // 节点已删除
        if (this.item().isBeDeleted()) {
            glyph.setColor(Color.RED);
        } else if (this.item().isDataUnsaved()) { // 节点数据未保存
            glyph.setColor(Color.ORANGE);
        } else if (this.item().isBeChanged()) { // 节点已更新
            glyph.setColor(Color.PURPLE);
        } else if (this.item().isBeChildChanged()) { // 子节点已更新
            glyph.setColor(Color.BROWN);
        } else {
            super.flushGraphicColor();
        }
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }

    private SimpleStringProperty pathProperty;

    public ObservableValue<String> pathProperty() {
        if (this.pathProperty == null) {
            this.pathProperty = new SimpleStringProperty();
        }
        return this.pathProperty;
    }

    private SimpleStringProperty extraProperty;

    public ObservableValue<String> extraProperty() {
        if (this.extraProperty == null) {
            this.extraProperty = new SimpleStringProperty();
        }
        return this.extraProperty;
    }
}
