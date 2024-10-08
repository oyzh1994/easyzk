package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import javafx.scene.Cursor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKConnectTab extends DynamicTab {

    /**
     * 标签打开时间
     */
    @Getter
    private final long openedTime = System.currentTimeMillis();

    /**
     * zk树节点
     */
    public ZKConnectTreeItem treeItem() {
        return this.getProp("_treeItem");
    }

    /**
     * 执行初始化
     *
     * @param treeItem zk树节点
     */
    public void init(@NonNull ZKConnectTreeItem treeItem) {
        if (treeItem != this.treeItem()) {
            this.setProp("_treeItem", treeItem);
            // 初始化
            this.controller().init(treeItem);
            // 刷新tab
            this.flush();
            // 检查节点状态
            this.checkStatus();
        }
    }

    /**
     * 检查状态
     */
    public void checkStatus() {
        if (this.treeItem() == null) {
            return;
        }
    }

    @Override
    public void flushTitle() {
        if (this.treeItem() == null) {
            return;
        }
    }

    @Override
    public void flushGraphic() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
    }

    @Override
    public void flushGraphicColor() {
        if (this.treeItem() == null) {
            return;
        }
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
    }

    /**
     * 获取节点数据组件
     *
     * @return 节点数据组件
     */
    public RichDataTextAreaPane getDataNode() {
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
        return (ZKConnectTabContent) super.controller();
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
