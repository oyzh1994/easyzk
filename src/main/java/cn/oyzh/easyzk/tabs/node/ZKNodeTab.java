package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.common.dto.FriendlyInfo;
import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.util.TextUtil;
import cn.oyzh.easyzk.controller.acl.ZKACLAddController;
import cn.oyzh.easyzk.controller.acl.ZKACLUpdateController;
import cn.oyzh.easyzk.controller.node.ZKNodeAddController;
import cn.oyzh.easyzk.controller.node.ZKNodeQRCodeController;
import cn.oyzh.easyzk.controller.node.ZKNodeSearchController;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.auth.ZKAuthAuthedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeACLAddedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeACLUpdatedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeAddedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeChangedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeCreatedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeRemovedEvent;
import cn.oyzh.easyzk.event.search.ZKSearchFinishEvent;
import cn.oyzh.easyzk.event.search.ZKSearchTriggerEvent;
import cn.oyzh.easyzk.fx.ZKACLControl;
import cn.oyzh.easyzk.fx.ZKACLTableView;
import cn.oyzh.easyzk.fx.filter.ZKNodeFilterTextField;
import cn.oyzh.easyzk.fx.filter.ZKNodeFilterTypeComboBox;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeView;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.combobox.CharsetComboBox;
import cn.oyzh.fx.gui.page.PageBox;
import cn.oyzh.fx.gui.svg.pane.CollectSVGPane;
import cn.oyzh.fx.gui.svg.pane.SortSVGPane;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.node.NodeResizeHelper;
import cn.oyzh.fx.plus.thread.RenderService;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTypeComboBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;
import lombok.Getter;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.Stat;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * zk节点tab
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKNodeTab extends DynamicTab {

    public ZKNodeTab(ZKConnectTreeItem treeItem) {
        // 初始化
        this.controller().init(treeItem);
        // 刷新tab
        this.flush();
    }

    @Override
    public void initNode() {
        super.initNode();
        this.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                ZKEventUtil.searchClose(this.zkConnect());
            }
        });
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
        return this.treeItem() == null ? null : this.treeItem().client();
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

    /**
     * 执行搜索
     */
    public void doSearch() {
        this.controller().doSearch();
    }


}
