package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.NonNull;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
public class ZKNodeTreeItemValue extends ZKTreeItemValue {

    public ZKNodeTreeItemValue(@NonNull ZKNodeTreeItem item) {
        super.setProp("_item", item);
    }

    private ZKNodeTreeItem getItem() {
        return this.getProp("_item");
    }

    private boolean isInvalid() {
        return !this.hasProp("_item");
    }

    @Override
    public void flushGraphic() {
        if (this.isInvalid()) {
            return;
        }
        SVGGlyph glyph = this.graphic();
        if (glyph != null && glyph.isWaiting()) {
            return;
        }
        // 设置图标
        if (glyph == null) {
            glyph = new SVGGlyph("/font/file-text.svg", 10);
            glyph.disableTheme();
            this.graphic(glyph);
        }
    }

    @Override
    public void flush() {
        if (this.isInvalid()) {
            return;
        }
        super.flush();
        this.flushNum(this.getItem().getNumChildren(), this.getItem().getChildrenSize());
    }

    /**
     * 刷新图形颜色
     */
    @Override
    public void flushGraphicColor() {
        if (this.isInvalid()) {
            return;
        }
        // 获取图形符号
        SVGGlyph glyph = this.graphic();
        // 节点已删除
        if (this.getItem().isBeDeleted()) {
            glyph.setColor(Color.RED);
        } else if (this.getItem().isDataUnsaved()) { // 节点数据未保存
            glyph.setColor(Color.ORANGE);
        } else if (this.getItem().isBeChanged()) { // 节点已更新
            glyph.setColor(Color.PURPLE);
        } else if (this.getItem().isBeChildChanged()) { // 子节点已更新
            glyph.setColor(Color.BROWN);
        } else {
            super.flushGraphicColor();
        }
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }

    /**
     * 刷新节点数量
     *
     * @param totalNum 子节点总数量
     * @param showNum  子节点显示数量
     */
    public void flushNum(Integer totalNum, Integer showNum) {
        // 寻找组件
        FXText text = (FXText) this.lookup("#num");
        if (text == null) {
            text = new FXText();
            text.disableTheme();
            this.addChild(text);
            text.setId("num");
            text.setFill(Color.valueOf("#228B22"));
            HBox.setMargin(text, new Insets(0, 0, 0, 3));
        }
        if (totalNum == null || totalNum == 0) {
            text.setText("");
        } else if (showNum == null || showNum.intValue() == totalNum.intValue()) {
            text.setText("(" + totalNum + ")");
        } else {
            text.setText("(" + showNum + "/" + totalNum + ")");
        }
    }

    @Override
    public String name() {
        if (this.isInvalid()) {
            return null;
        }
        return this.getItem().decodeNodeName();
    }
}
