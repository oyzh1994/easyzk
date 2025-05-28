package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKNodeTab extends RichTab {

    public ZKNodeTab(ZKConnectTreeItem treeItem) {
        // 初始化
        this.controller().init(treeItem);
        // 刷新tab
        this.flush();
    }

//    @Override
//    public void initNode() {
//        super.initNode();
//        this.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (!newValue) {
//                ZKEventUtil.searchClose(this.zkConnect());
//            }
//        });
//    }

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

    @Override
    protected String getTabTitle() {
        if (this.activeItem() == null) {
            return this.treeItem().connectName();
        }
        return this.treeItem().connectName() + "#" + this.activeItem().decodeNodePath();
    }

    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph graphic = this.treeItem().itemGraphic();
        if (graphic == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null || !StringUtil.notEquals(glyph.getUrl(), graphic.getUrl())) {
            glyph = graphic.clone();
            glyph.disableTheme();
            this.setGraphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
//        SVGGlyph graphic = this.treeItem().itemGraphic();
//        if (graphic == null) {
//            return;
//        }
//        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
//        if (glyph == null) {
//            return;
//        }
//        if (graphic.getColor() != glyph.getColor()) {
//            glyph.setColor(graphic.getColor());
//        }
        super.flushGraphicColor();
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.treeItem() == null ? null : this.treeItem().getClient();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKConnect zkConnect() {
        return this.treeItem() == null ? null : this.treeItem().value();
    }

    @Override
    public ZKNodeTabController controller() {
        return (ZKNodeTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/node/zkNodeTab.fxml";
    }

    /**
     * 恢复数据
     *
     * @param data 历史数据
     */
    public void restoreData(byte[] data) {
        this.controller().restoreData(data);
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        if (this.controller().getTreeView().hasUnsavedData() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            event.consume();
        } else {
            super.onTabCloseRequest(event);
        }
    }

//    /**
//     * 执行搜索
//     */
//    public void doSearch() {
//        this.controller().doSearch();
//    }
}
