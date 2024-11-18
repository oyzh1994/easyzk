package cn.oyzh.easyzk.tabs.connect;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
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
        return this.activeItem().decodeNodePath() + "#" + this.treeItem().infoName();
    }

    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        boolean changed = false;
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            changed = true;
        } else if (this.activeItem() == null) {
            SVGGlyph glyph1 = this.treeItem().graphic();
            if (StringUtil.notEquals(glyph.getUrl(), glyph1.getUrl())) {
                changed = true;
            }
        } else {
            SVGGlyph glyph1 = this.activeItem().valueGraphic();
            if (StringUtil.notEquals(glyph.getUrl(), glyph1.getUrl())) {
                changed = true;
            }
        }
        if (changed) {
            if (this.activeItem() == null) {
                glyph = this.treeItem().graphic().clone();
                glyph.disableTheme();
            } else {
                glyph = this.activeItem().valueGraphic().clone();
                glyph.disableTheme();
            }
            this.setGraphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (this.activeItem() != null) {
            SVGGlyph glyph2 = this.activeItem().valueGraphic();
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
