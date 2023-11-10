package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.ZKNodeTreeItem;
import cn.oyzh.fx.plus.controls.rich.FlexRichTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGLabel;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKNodeTab extends DynamicTab {

    /**
     * zk树节点
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private ZKNodeTreeItem treeItem;

    /**
     * 执行初始化
     *
     * @param treeItem zk树节点
     */
    public void init(@NonNull ZKNodeTreeItem treeItem) {
        this.treeItem = treeItem;
        // 初始化
        this.controller().init(treeItem);
        // 刷新tab
        this.flush();
    }

    /**
     * 检查状态
     */
    public void checkStatus() {
        // 节点被移除
        if (this.treeItem.isBeDeleted()) {
            if (!this.treeItem.isIgnoreDeleted()) {
                if (MessageBox.confirm("此节点已被其他连接删除，是否移除此节点？")) {
                    this.treeItem.remove();
                    this.closeTab();
                } else {
                    this.treeItem.setIgnoreDeleted(true);
                }
            }
        } else if (this.treeItem.isBeUpdated()) { // 节点被更新
            if (!this.treeItem.isIgnoreUpdated()) {
                if (MessageBox.confirm("此节点数据已被其他连接修改，是否更新数据？")) {
                    this.treeItem.applyUpdate();
                    this.controller().init(this.treeItem);
                    this.treeItem.flushGraphic();
                } else {
                    this.treeItem.setIgnoreUpdated(true);
                }
            }
        }
    }

    @Override
    public void flushTitle() {
        SVGLabel graphic = (SVGLabel) this.getGraphic();
        // 设置文本
        graphic.setText("（" + this.treeItem.infoName() + "）" + treeItem.decodeNodePath());
        // 设置提示文本
        this.setTipText("（" + this.treeItem.infoName() + "）" + this.treeItem.decodeNodePath());
    }

    @Override
    public void flushGraphic() {
        SVGLabel label = (SVGLabel) this.getGraphic();
        String svgUrl = this.treeItem.getSVGUrl();
        if (label == null || !Objects.equals(label.getUrl(), svgUrl)) {
            SVGLabel svgLabel = new SVGLabel(null, new SVGGlyph(svgUrl, "12"));
            svgLabel.setCursor(Cursor.DEFAULT);
            this.graphic(svgLabel);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGLabel label = (SVGLabel) this.getGraphic();
        SVGGlyph glyph = this.treeItem.itemValue().graphic();
        if (glyph != null && label != null && label.getColor() != glyph.getColor()) {
            this.fill(label.getColor());
        }
    }

    /**
     * 获取节点数据组件
     *
     * @return 节点数据组件
     */
    public FlexRichTextArea getDataNode() {
        return this.controller().getDataNode();
    }

    /**
     * 选中数据tab
     */
    public void selectDataTab() {
        this.controller().selectDataTab();
    }

    @Override
    public void reload() {
        this.controller().reload();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.treeItem.info();
    }

    @Override
    public ZKNodeTabContent controller() {
        return (ZKNodeTabContent) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/node/zkNodeTabContent.fxml";
    }
}
