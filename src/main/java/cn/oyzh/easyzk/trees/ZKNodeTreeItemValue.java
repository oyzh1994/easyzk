package cn.oyzh.easyzk.trees;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.text.FXText;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class ZKNodeTreeItemValue extends ZKTreeItemValue {

    private final ZKNodeTreeItem treeItem;

    public ZKNodeTreeItemValue(@NonNull ZKNodeTreeItem treeItem) {
        this.treeItem = treeItem;
        this.flushGraphic();
        this.flushText();
        this.flushGraphicColor();
        treeItem.dataProperty().addListener((observableValue, bytes, t1) -> this.flushStatus());
        treeItem.childNumProperty().addListener((observableValue, bytes, t1) -> this.flushChildNum());
    }

    @Override
    public String name() {
        return this.treeItem.decodeNodeName();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph curr = this.graphic();
        String svgUrl = this.getSVGUrl();
        // 设置图标
        if (curr == null || !Objects.equals(curr.getUrl(), svgUrl)) {
            this.graphic(new SVGGlyph(svgUrl, "12"));
            ZKEventUtil.graphicChanged(this.treeItem);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = this.graphic();
        if (this.treeItem.isBeDeleted()) {
            if (glyph.getColor() != Color.RED) {
                glyph.setColor(Color.RED);
                ZKEventUtil.graphicColorChanged(this.treeItem);
            }
        } else if (this.treeItem.dataUnsaved()) {
            if (glyph.getColor() != Color.ORANGE) {
                glyph.setColor(Color.ORANGE);
                ZKEventUtil.graphicColorChanged(this.treeItem);
            }
        } else if (this.treeItem.isBeUpdated()) {
            if (glyph.getColor() != Color.PURPLE) {
                glyph.setColor(Color.PURPLE);
                ZKEventUtil.graphicColorChanged(this.treeItem);
            }
        } else if (glyph.getColor() != Color.BLACK) {
            glyph.setColor(Color.BLACK);
            ZKEventUtil.graphicColorChanged(this.treeItem);
        }
    }

    /**
     * 获取图标地址
     *
     * @return 图标地址
     */
    public String getSVGUrl() {
        ZKNode value = this.treeItem.value();
        // 需要认证
        if (ZKAuthUtil.isNeedAuth(value, this.treeItem.zkClient())) {
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
        if (treeItem.loaded()) {
            return "/font/folder-open.svg";
        }

        // 父节点，未加载
        return "/font/folder.svg";
    }

    /**
     * 初始化子节点数量组件
     */
    private void flushChildNum() {
        int childNum = this.treeItem.childNum();
        FXText text = (FXText) this.getChild(2);
        if (childNum == -1) {
            this.removeChild(text);
        } else if (text == null) {
            text = new FXText("(" + childNum + ")");
            text.setFill(Color.valueOf("#228B22"));
            this.addChild(text);
            HBox.setMargin(text, new Insets(0, 0, 0, 3));
        } else {
            text.setText("(" + childNum + ")");
        }
    }

    public void flushStatus() {
        FXText text = (FXText) this.getChild(1);
        if (this.treeItem.isBeDeleted()) {
            text.setFill(Color.RED);
        } else if (this.treeItem.dataUnsaved()) {
            text.setFill(Color.ORANGE);
        } else if (this.treeItem.isBeUpdated()) {
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
}
