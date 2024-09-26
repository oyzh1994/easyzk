package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.ZKTreeItemValue;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.NonNull;

import java.util.Objects;


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
        String svgUrl = this.getSVGUrl();
        // 设置图标
        if (glyph == null || !Objects.equals(glyph.getUrl(), svgUrl)) {
            glyph = new SVGGlyph(svgUrl, 10);
            glyph.disableTheme();
            this.graphic(glyph);
            ZKEventUtil.graphicChanged(this.getItem());
        }
    }

    /**
     * 刷新内容
     */
    public void flush() {
        if (this.isInvalid()) {
            return;
        }
        this.flushGraphic();
        this.flushText();
        this.flushGraphicColor();
        this.flushNum(this.getItem().value().getNumChildren(), this.getItem().getChildren().size());
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
        // 如果节点被删除
        if (this.getItem().isBeDeleted()) {
            // 如果图形颜色不是红色
            if (glyph.getColor() != Color.RED) {
                // 设置图形颜色为红色
                glyph.setColor(Color.RED);
                // 触发图形颜色改变事件
                ZKEventUtil.graphicColorChanged(this.getItem());
            }
        } else if (this.getItem().dataUnsaved()) { // 如果节点数据未保存
            // 如果图形颜色不是橙色
            if (glyph.getColor() != Color.ORANGE) {
                // 设置图形颜色为橙色
                glyph.setColor(Color.ORANGE);
                // 触发图形颜色改变事件
                ZKEventUtil.graphicColorChanged(this.getItem());
            }
        } else if (this.getItem().isBeUpdated()) { // 如果节点已被更新
            // 如果图形颜色不是紫色
            if (glyph.getColor() != Color.PURPLE) {
                // 设置图形颜色为紫色
                glyph.setColor(Color.PURPLE);
                // 触发图形颜色改变事件
                ZKEventUtil.graphicColorChanged(this.getItem());
            }
        } else {
            super.flushGraphicColor();
            // 触发图形颜色改变事件
            ZKEventUtil.graphicColorChanged(this.getItem());
        }
    }

    /**
     * 获取图标地址
     *
     * @return 图标地址
     */
    public String getSVGUrl() {
        if (this.isInvalid()) {
            return null;
        }
        ZKNode value = this.getItem().value();
        // 需要认证
        if (ZKAuthUtil.isNeedAuth(value, this.getItem().client())) {
            return "/fx-plus/font/lock.svg";
        }
        // 临时节点
        if (value.ephemeral()) {
            return "/font/temp.svg";
        }
        // 子节点
        if (value.subNode()) {
            return "/font/file-text.svg";
        }
        // 父节点，已加载
        if (this.getItem().loaded()) {
            return "/font/folder-open.svg";
        }
        // 父节点，未加载
        return "/font/folder.svg";
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
