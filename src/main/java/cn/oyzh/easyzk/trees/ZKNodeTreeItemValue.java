package cn.oyzh.easyzk.trees;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.util.FXUtil;
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

    /**
     * 子节点总数量
     */
    private Integer childNum;

    /**
     * 子节点显示数量
     */
    private Integer showChildNum;

    /**
     * 树节点
     */
    private final ZKNodeTreeItem item;

    public ZKNodeTreeItemValue(@NonNull ZKNodeTreeItem item) {
        this.item = item;
//        this.flushGraphic();
//        this.name(item.decodeNodeName());
//        this.flushGraphicColor();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph curr = this.graphic();
        if (curr != null && curr.isWaiting()) {
            return;
        }
        String svgUrl = this.getSVGUrl();
        // 设置图标
        if (curr == null || !Objects.equals(curr.getUrl(), svgUrl)) {
            this.graphic(new SVGGlyph(svgUrl, "12"));
            ZKEventUtil.graphicChanged(this.item);
        }
    }

    /**
     * 刷新内容
     */
    public void flush() {
        this.flushGraphic();
        this.flushText();
        this.flushGraphicColor();
        this.flushNum(this.item.value().getNumChildren(), this.item.getChildren().size());
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = this.graphic();
        if (this.item.isBeDeleted()) {
            if (glyph.getColor() != Color.RED) {
                glyph.setColor(Color.RED);
                ZKEventUtil.graphicColorChanged(this.item);
            }
        } else if (this.item.dataUnsaved()) {
            if (glyph.getColor() != Color.ORANGE) {
                glyph.setColor(Color.ORANGE);
                ZKEventUtil.graphicColorChanged(this.item);
            }
        } else if (this.item.isBeUpdated()) {
            if (glyph.getColor() != Color.PURPLE) {
                glyph.setColor(Color.PURPLE);
                ZKEventUtil.graphicColorChanged(this.item);
            }
        } else if (glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
            ZKEventUtil.graphicColorChanged(this.item);
        }
    }

    /**
     * 获取图标地址
     *
     * @return 图标地址
     */
    public String getSVGUrl() {
        ZKNode value = this.item.value();
        // 需要认证
        if (ZKAuthUtil.isNeedAuth(value, this.item.client())) {
            return "/font/lock.svg";
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
        if (this.item.loaded()) {
            return "/font/folder-open.svg";
        }
        // 父节点，未加载
        return "/font/folder.svg";
    }

    /**
     * 刷新子节点数量
     */
    private void flushChildNum() {
        // 寻找组件
        FXText text = (FXText) this.lookup("#num");
        if (text == null) {
            text = new FXText();
            this.addChild(text);
            text.setId("num");
            text.setFill(Color.valueOf("#228B22"));
            HBox.setMargin(text, new Insets(0, 0, 0, 3));
        }
        if (this.childNum == null || this.childNum == 0) {
            text.setText("");
        } else if (this.showChildNum == null || this.showChildNum == this.childNum.intValue()) {
            text.setText("(" + this.childNum + ")");
        } else {
            text.setText("(" + this.showChildNum + "/" + this.childNum + ")");
        }
    }

    /**
     * 刷新状态
     */
    public void flushStatus() {
        FXText text = this.text();
        if (this.item.isBeDeleted()) {
            text.setFill(Color.RED);
        } else if (this.item.dataUnsaved()) {
            text.setFill(Color.ORANGE);
        } else if (this.item.isBeUpdated()) {
            text.setFill(Color.PURPLE);
        } else {
            text.setFill(Color.BLACK);
        }
        this.flushGraphic();
        this.flushGraphicColor();
    }

    @Override
    public SVGGlyph graphic() {
        return (SVGGlyph) super.graphic();
    }

    /**
     * 刷新节点数量
     *
     * @param childNum     子节点总数量
     * @param showChildNum 子节点显示数量
     */
    public void flushNum(Integer childNum, Integer showChildNum) {
        if (childNum != null) {
            this.childNum = childNum;
        }
        this.showChildNum = showChildNum;
        this.flushChildNum();
    }

    @Override
    public String name() {
        return this.item.decodeNodeName();
    }
}
