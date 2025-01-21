package cn.oyzh.easyzk.query;

import cn.oyzh.fx.gui.svg.glyph.FunctionSVGGlyph;
import cn.oyzh.fx.gui.svg.glyph.ProcedureSVGGlyph;
import cn.oyzh.fx.gui.svg.glyph.ViewSVGGlyph;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.label.FlexLabel;
import cn.oyzh.fx.plus.controls.list.FlexListView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.util.ControlUtil;
import cn.oyzh.fx.plus.util.MouseUtil;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/02/21
 */
public class ZKQueryPromptListView extends FlexListView<FlexHBox> {

    {
        this.setRealWidth(480);
        this.setRealHeight(360);
        this.setPadding(new Insets(0));
    }

    /**
     * 选中项坐标
     */
    private volatile int currentPickIndex = -1;

    /**
     * 节点选中事件
     */
    @Getter
    @Setter
    private Runnable onItemPicked;

    @Override
    public void select(int index) {
        if (index < 0) {
            index = 0;
        }
        if (index >= this.getItems().size()) {
            index = this.getItems().size() - 1;
        }
        super.select(index);
        // 应用背景色
        this.applyBackground(index);
    }

    /**
     * 选中下一个
     */
    public synchronized void pickNext() {
        this.select(this.currentPickIndex + 1);
    }

    /**
     * 选中上一个
     */
    public synchronized void pickPrev() {
        this.select(this.currentPickIndex - 1);
    }

    /**
     * 是否有选中项
     *
     * @return 结果
     */
    public synchronized boolean hasPicked() {
        FlexHBox box = this.getSelectedItem();
        return box != null && this.currentPickIndex != -1;
    }

    /**
     * 获取选中项
     *
     * @return 结果
     */
    public ZKQueryPromptItem getPickedItem() {
        FlexHBox hBox = this.getSelectedItem();
        if (hBox != null) {
            ZKQueryPromptItem item = hBox.getProp("item");
            if (item != null) {
                this.applyBackground(-1);
                return item;
            }
        }
        return null;
    }

    /**
     * 应用背景色
     *
     * @param pickedIndex 选择位置的索引
     */
    private void applyBackground(int pickedIndex) {
        if (this.currentPickIndex >= 0) {
            try {
                FlexHBox hBox1 = (FlexHBox) this.getItem(this.currentPickIndex);
                if (hBox1 != null) {
                    hBox1.setBackground(null);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (pickedIndex >= 0) {
            try {
                FlexHBox hBox1 = (FlexHBox) this.getItem(pickedIndex);
                if (hBox1 != null) {
                    hBox1.setBackground(ControlUtil.background(Color.DEEPSKYBLUE));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.currentPickIndex = pickedIndex;
    }

    /**
     * 执行初始化
     *
     * @param items 提示
     */
    public void init(List<ZKQueryPromptItem> items) {
        // 应用背景色
        this.applyBackground(-1);
        // 初始化数据
        List<FlexHBox> boxList = new ArrayList<>();
        // 初始化节点内容
        for (ZKQueryPromptItem item : items) {
            FlexHBox box = new FlexHBox();
            this.initBox(box);
            // 提示组件
            SVGLabel promptLabel = this.initPromptLabel(item);
            box.addChild(promptLabel);
            // 额外组件
            FlexLabel extLabel = this.initExtLabel(item);
            if (extLabel != null) {
                box.addChild(extLabel);
            }
            box.setProp("item", item);
            boxList.add(box);
            System.out.println(item + "=" + item.getContent());
        }
        System.out.println("----------->");
        this.setItem(boxList);
    }

    /**
     * 初始化提示组件
     *
     * @param item 提示词
     * @return 组件
     */
    private SVGLabel initPromptLabel(ZKQueryPromptItem item) {
        SVGLabel label = null;
         if (item.isKeywordType()) {
            SVGGlyph svgGlyph = new SVGGlyph("/font/keywords.svg", "12");
            svgGlyph.setColor(Color.BLACK);
            label = new SVGLabel(item.getContent(), svgGlyph);
        } else if (item.isNodeType()) {
            SVGGlyph svgGlyph = new SVGGlyph("/font/file-text.svg", "12");
            svgGlyph.setColor(Color.BLACK);
            label = new SVGLabel(item.getContent(), svgGlyph);
            label.setRealWidth(240);
        } else if (item.isParamType()) {
            SVGGlyph svgGlyph = new SVGGlyph("/font/param.svg", "12");
            svgGlyph.setColor(Color.BLACK);
            label = new SVGLabel(item.getContent(), svgGlyph);
        }
        if (label != null) {
            label.setTipText(item.getContent());
        }
        return label;
    }

    /**
     * 初始化额外信息组件
     *
     * @param item 提示词
     * @return 组件
     */
    private FlexLabel initExtLabel(ZKQueryPromptItem item) {
//        FlexLabel label = null;
//        if (item.isTableType() || item.isViewType() || item.isColumnType()) {
//            label = new FlexLabel(item.getExtContent());
//            label.setTextFill(Color.valueOf("#D3D3D3"));
//        }
//        if (label != null) {
//            label.setTipText(item.getContent());
//        }
//        return label;
        return null;
    }

    /**
     * 初始化提示词组件
     *
     * @param box 提示词组件
     */
    private void initBox(FlexHBox box) {
        // 设置高度
        box.setRealHeight(20);
        // 设置内边距
        box.setPadding(new Insets(0));
        // 设置鼠标样式
        box.setCursor(Cursor.HAND);
        // 鼠标点击事件
        box.setOnMouseClicked(event -> {
            if (MouseUtil.isSingleClick(event)) {
                this.applyBackground(this.getItems().indexOf(box));
            } else if (this.onItemPicked != null) {
                this.onItemPicked.run();
            }
        });
    }
}
