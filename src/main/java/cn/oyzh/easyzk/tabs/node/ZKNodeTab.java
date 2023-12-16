package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.trees.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.rich.FlexRichTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.scene.Cursor;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKNodeTab extends DynamicTab {

    /**
     * 标签打开时间
     */
    @Getter
    private final long openedTime = System.currentTimeMillis();

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
        if (treeItem != this.treeItem) {
            this.treeItem = treeItem;
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
        // 设置文本
        this.setText(this.treeItem.infoName() + "-" + this.treeItem.decodeNodePath());
        // 设置提示文本
        this.setTipText(this.treeItem.infoName() + "-" + this.treeItem.decodeNodePath());
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        String svgUrl = this.treeItem.getSVGUrl();
        if (glyph == null || !Objects.equals(glyph.getUrl(), svgUrl)) {
            glyph = new SVGGlyph(svgUrl, "12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    public void flushGraphicColor() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        SVGGlyph graphic = this.treeItem.graphic();
        if (glyph != null && graphic != null && graphic.getColor() != glyph.getColor()) {
            this.fill(graphic.getColor());
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

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.treeItem.client();
    }

    @Override
    public ZKNodeTabContent controller() {
        return (ZKNodeTabContent) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/node/zkNodeTabContent.fxml";
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = super.getMenuItems();
        MenuItem closeConnectTab = MenuItemExt.newItem("关闭当前连接", "关闭当前连接标签页", this::closeConnectTab);
        MenuItem closeOtherConnectTab = MenuItemExt.newItem("关闭其他连接", "关闭其他连接标签页", this::closeOtherConnectTab);
        items.add(4, closeConnectTab);
        items.add(5, closeOtherConnectTab);
        return items;
    }

    /**
     * 关闭当前连接tab
     */
    private void closeConnectTab() {
        FXUtil.runLater(() -> {
            List<Tab> list = new ArrayList<>();
            for (Tab tab : this.tabs()) {
                if (tab instanceof ZKNodeTab nodeTab && nodeTab.client() == this.treeItem.client()) {
                    list.add(tab);
                }
            }
            this.tabs().removeAll(list);
        });
    }

    /**
     * 关闭其他连接tab
     */
    private void closeOtherConnectTab() {
        FXUtil.runLater(() -> {
            List<Tab> list = new ArrayList<>();
            for (Tab tab : this.tabs()) {
                if (tab instanceof ZKNodeTab nodeTab && nodeTab.client() != this.treeItem.client()) {
                    list.add(tab);
                }
            }
            this.tabs().removeAll(list);
        });
    }
}
