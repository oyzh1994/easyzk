package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.theme.ThemeManager;
import javafx.scene.paint.Color;
import lombok.NonNull;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKConnectTab extends DynamicTab {

    /**
     * zk树节点
     */
    public ZKConnectTreeItem treeItem() {
        return this.controller().getTreeItem();
    }

    /**
     * zk树节点
     */
    public ZKNodeTreeItem activeItem() {
        return this.controller().getActiveItem();
    }

    /**
     * 执行初始化
     *
     * @param treeItem zk树节点
     */
    public void init(@NonNull ZKConnectTreeItem treeItem) {
        if (treeItem != this.treeItem()) {
            // 初始化
            this.controller().init(treeItem);
            // 刷新tab
            this.flush();
        }
    }

    @Override
    protected String getTabTitle() {
        if (this.activeItem() == null) {
            return this.treeItem().infoName();
        }
        return this.activeItem().decodeNodePath() + "#" + this.treeItem().infoName();
    }

    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/file-text.svg", 10);
            glyph.disableTheme();
            this.setGraphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (this.activeItem() != null) {
            SVGGlyph glyph2 = this.activeItem().graphic();
            glyph.setColor(glyph2.getColor());
        } else if (ThemeManager.isDarkMode()) {
            glyph.setColor(Color.WHITE);
        } else {
            glyph.setColor(Color.BLACK);
        }
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.treeItem() == null ? null : this.treeItem().client();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.treeItem() == null ? null : this.treeItem().value();
    }

    @Override
    public ZKConnectTabContent controller() {
        return super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/node/zkConnectTabContent.fxml";
    }

    /**
     * 恢复数据
     *
     * @param data 历史数据
     */
    public void restoreData(byte[] data) {
        this.controller().restoreData(data);
    }

    // public void onNodeAdd(String nodePath) {
    //     this.controller().onNodeAdd(nodePath);
    // }
    //
    // public void onNodeAdded(String nodePath) {
    //     this.controller().onNodeAdded(nodePath);
    // }
    //
    // public void onNodeDeleted(String nodePath) {
    //     this.controller().onNodeDeleted(nodePath);
    // }
    //
    // public void onNodeUpdated(String nodePath) {
    //     this.controller().onNodeUpdated(nodePath);
    // }
    //
    // public void onNodeACLChanged() {
    //     this.controller().initACL();
    // }
}
