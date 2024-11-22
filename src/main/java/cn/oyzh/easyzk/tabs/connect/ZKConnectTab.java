package cn.oyzh.easyzk.tabs.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.theme.ThemeManager;
import lombok.NonNull;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKConnectTab extends DynamicTab {

    public ZKConnectTab(ZKConnectTreeItem treeItem) {
        this.init(treeItem);
    }

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
        // 初始化
        this.controller().init(treeItem);
        // 刷新tab
        this.flush();
    }

    @Override
    protected String getTabTitle() {
        if (this.activeItem() == null) {
            return this.treeItem().infoName();
        }
        return this.treeItem().infoName() + "#" + this.activeItem().decodeNodePath();
    }

    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph graphic1 = (SVGGlyph) this.treeItem().getGraphic();
        if (graphic1 == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null || !StringUtil.notEquals(glyph.getUrl(), graphic1.getUrl())) {
            glyph = graphic1.clone();
            glyph.disableTheme();
            this.setGraphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph graphic1 = (SVGGlyph) this.treeItem().getGraphic();
        if (graphic1 == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            return;
        }
        if (graphic1.getColor() != glyph.getColor()) {
            glyph.setColor(graphic1.getColor());
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
        return "/tabs/connect/zkConnectTabContent.fxml";
    }

    /**
     * 恢复数据
     *
     * @param data 历史数据
     */
    public void restoreData(byte[] data) {
        this.controller().restoreData(data);
    }
}
