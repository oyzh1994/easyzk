package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import lombok.Getter;
import lombok.NonNull;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKConnectTab extends DynamicTab {

    // /**
    //  * 标签打开时间
    //  */
    // @Getter
    // private final long openedTime = System.currentTimeMillis();

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

    // @Override
    // public void flushTitle() {
    //     if (this.treeItem() == null) {
    //         return;
    //     }
    //     // 设置文本
    //     if (this.activeItem() == null) {
    //         this.setText(this.treeItem().infoName());
    //     } else {
    //         this.setText(this.activeItem().decodeNodePath() + "#" + this.treeItem().infoName());
    //     }
    // }

    @Override
    protected String getTabTitle() {
        if (this.activeItem() == null) {
            return this.treeItem().infoName();
        }
        return this.activeItem().decodeNodePath() + "#" + this.treeItem().infoName();
    }

    // @Override
    // public void flushGraphic() {
    //     if (this.treeItem() == null) {
    //         return;
    //     }
    //     SVGGlyph glyph1 = (SVGGlyph) this.getGraphic();
    //     if (this.activeItem() == null) {
    //         if (glyph1 == null) {
    //             this.setGraphic(this.treeItem().graphic().clone());
    //         }
    //     } else {
    //         SVGGlyph glyph2 = this.activeItem().graphic();
    //         if (glyph1 == null || StringUtil.notEquals(glyph1.getUrl(), glyph2.getUrl())) {
    //             this.setGraphic(glyph2.clone());
    //         }
    //     }
    // }

    @Override
    public void flushGraphicColor() {
        if (this.activeItem() != null) {
            SVGGlyph glyph1 = (SVGGlyph) this.getGraphic();
            SVGGlyph glyph2 = this.activeItem().graphic();
            if (glyph1 != null && glyph1.getColor() != glyph2.getColor()) {
                glyph1.setColor(glyph2.getColor());
            }
        }
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
        return null;
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        if (this.treeItem() == null) {
            return null;
        }
        return this.treeItem().client();
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
}
