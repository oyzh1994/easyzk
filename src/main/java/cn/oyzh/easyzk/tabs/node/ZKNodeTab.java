package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import cn.oyzh.easyzk.tabs.ZKBaseTab;
import cn.oyzh.fx.plus.controls.FlexTextArea;
import cn.oyzh.fx.plus.ext.FXMLLoaderExt;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.svg.SVGLabel;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;
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
public class ZKNodeTab extends ZKBaseTab {

    {
        this.setClosable(true);
        this.setOnCloseRequest(event -> {
            // 取消当前节点的选中
            if (this.treeItem != null && this.treeItem.treeView().getSelectedItem() == this.treeItem) {
                this.treeItem.treeView().select(this.treeItem.root());
            }
        });
    }

    /**
     * zk树组件
     */
    @Getter
    @Accessors(fluent = true, chain = true)
    private ZKNodeTreeItem treeItem;

    /**
     * 内容Controller
     */
    private ZKNodeTabContentController contentController;

    /**
     * 执行初始化
     *
     * @param treeItem zk树节点
     */
    public void init(@NonNull ZKNodeTreeItem treeItem) {
        this.treeItem = treeItem;
        // 初始化
        this.contentController.init(treeItem);
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
                    this.contentController.init(this.treeItem);
                    this.treeItem.flushGraphic();
                } else {
                    this.treeItem.setIgnoreUpdated(true);
                }
            }
        }
    }

    /**
     * 刷新tab
     */
    public void flush() {
        FXUtil.runWait(() -> {
            this.flushGraphic();
            this.flushTitle();
            this.flushGraphicColor();
        });
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
            SVGLabel graphic1 = new SVGLabel(null, new SVGGlyph(svgUrl, "12"));
            graphic1.setCursor(Cursor.DEFAULT);
            FXUtil.runLater(() -> this.setGraphic(graphic1));
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGLabel graphic = (SVGLabel) this.getGraphic();
        SVGGlyph glyph = this.treeItem.itemValue().graphic();
        if (glyph != null && graphic != null && graphic.getColor() != glyph.getColor()) {
            graphic.setColor(glyph.getColor());
            graphic.setTextFill(glyph.getColor());
        }
    }

    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        Node content = loaderExt.load("/tabs/node/zkNodeTabContent.fxml");
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.contentController = loaderExt.getController();
        this.setContent(content);
    }

    /**
     * 获取节点数据组件
     *
     * @return 节点数据组件
     */
    public FlexTextArea getDataNode() {
        return this.contentController.getDataNode();
    }

    /**
     * 选中数据tab
     */
    public void selectDataTab() {
        this.contentController.selectDataTab();
    }

    /**
     * 重新载入
     */
    public void reload() {
        this.contentController.reload();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.treeItem.info();
    }
}
