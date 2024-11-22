package cn.oyzh.easyzk.trees.node;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.fx.gui.svg.glyph.LockSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
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
        super(item);
    }

    @Override
    protected ZKNodeTreeItem item() {
        return (ZKNodeTreeItem) super.item();
    }

    @Override
    public SVGGlyph graphic() {
        if (this.graphic != null && this.graphic.isWaiting()) {
            return this.graphic;
        }
        boolean changed = false;
        if (this.graphic == null) {
            changed = true;
        } else if (this.item().isNeedAuth() && StringUtil.notEquals(this.graphic.getProp("_type"), "3")) {
            changed = true;
        } else if (this.item().isEphemeral() && StringUtil.notEquals(this.graphic.getProp("_type"), "2")) {
            changed = true;
        } else if (StringUtil.notEquals(this.graphic.getProp("_type"), "1")) {
            changed = true;
        }
        if (changed) {
            if (this.item().isNeedAuth()) {
                this.graphic = new LockSVGGlyph("11");
                this.graphic.setProp("_type", "3");
            } else if (this.item().isEphemeral()) {
                this.graphic = new SVGGlyph("/font/temp.svg", 11);
                this.graphic.setProp("_type", "2");
            } else {
                this.graphic = new SVGGlyph("/font/file-text.svg", 11);
                this.graphic.setProp("_type", "1");
            }
            this.graphic.disableTheme();
        }
        return super.graphic();
    }

    @Override
    public String extra() {
        String extra;
        int showNum = this.item().getChildrenSize();
        Integer totalNum = this.item().getNumChildren();
        if (totalNum == null || totalNum == 0) {
            extra = null;
        } else if (showNum == totalNum) {
            extra = "(" + totalNum + ")";
        } else {
            extra = "(" + showNum + "/" + totalNum + ")";
        }
        return extra;
    }

    @Override
    public Color graphicColor() {
        // 节点已删除
        if (this.item().isBeDeleted()) {
            return Color.RED;
        } else if (this.item().isDataUnsaved()) { // 节点数据未保存
            return Color.ORANGE;
        } else if (this.item().isBeChanged()) { // 节点已更新
            return Color.PURPLE;
        } else if (this.item().isBeChildChanged()) { // 子节点已更新
            return Color.BROWN;
        }
        return super.graphicColor();
    }

    @Override
    public String name() {
        return this.item().decodeNodeName();
    }
}
