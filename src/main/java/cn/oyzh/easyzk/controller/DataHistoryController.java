package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.dto.ZKDataHistoryVO;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKHistoryAddEvent;
import cn.oyzh.easyzk.event.ZKHistoryShowEvent;
import cn.oyzh.easyzk.event.ZKTabChangedEvent;
import cn.oyzh.easyzk.store.ZKDataHistoryStore;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.controller.SubController;
import cn.oyzh.fx.plus.controls.svg.DeleteSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.UndoSVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * zk数据历史业务
 *
 * @author oyzh
 * @since 2024/04/23
 */
public class DataHistoryController extends SubController implements Initializable {

    /**
     * 根节点
     */
    @FXML
    private FXTab root;

    /**
     * 数据列表
     */
    @FXML
    private FlexTableView<ZKDataHistory> listTable;

    /**
     * 数据索id列
     */
    @FXML
    private TableColumn<ZKDataHistory, Integer> index;

    /**
     * 时间列
     */
    @FXML
    private TableColumn<ZKDataHistoryVO, String> saveTime;

    /**
     * 时间列
     */
    @FXML
    private TableColumn<ZKDataHistoryVO, String> dataSize;

    /**
     * 数据操作列
     */
    @FXML
    private FlexTableColumn<ZKDataHistoryVO, String> action;

    /**
     * zk历史储存
     */
    private final ZKDataHistoryStore historyStore = ZKDataHistoryStore.INSTANCE;

    /**
     * zk树节点
     */
    private ZKNodeTreeItem item;

    /**
     * 初始化列表控件
     */
    private void initTable() {
        // 操作栏初始化
        this.action.setCellFactory((cell) -> new FXTableCell<>() {
            private HBox hBox;

            @Override
            public Node initGraphic() {
                if (this.hBox == null) {
                    // 删除按钮
                    DeleteSVGGlyph del = new DeleteSVGGlyph("14");
                    del.setOnMousePrimaryClicked((event) -> delete(this.getTableItem()));
                    HBox.setMargin(del, new Insets(7, 0, 0, 5));

                    // 恢复按钮
                    UndoSVGGlyph restore = new UndoSVGGlyph("14");
                    restore.setOnMousePrimaryClicked((event) -> restore(this.getTableItem()));

                    this.hBox = new HBox(restore, del);
                    HBox.setMargin(del, new Insets(7, 0, 0, 5));
                    HBox.setMargin(restore, new Insets(7, 0, 0, 5));
                }
                return hBox;
            }
        });
    }

    /**
     * 删除历史
     *
     * @param history 历史记录
     */
    private void delete(ZKDataHistory history) {
        if (MessageBox.confirm(I18nResourceBundle.i18nString("base.delete", "base.data"))) {
            if (this.historyStore.delete(history)) {
                this.refresh();
            } else {
                MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
            }
        }
    }

    /**
     * 恢复历史
     *
     * @param history 历史记录
     */
    private void restore(ZKDataHistory history) {
        if (MessageBox.confirm(I18nResourceBundle.i18nString("base.restore", "base.data"))) {
            ZKEventUtil.historyRestore(history, this.item);
        }
    }

    /**
     * tab变化事件
     */
    @Subscribe
    public void tabChanged(ZKTabChangedEvent event) {
        Tab tab = event.data();
        if (tab instanceof ZKNodeTab tab1 && tab1.treeItem() != this.item) {
            this.item = tab1.treeItem();
            this.refresh();
        }
    }

    /**
     * 新增历史
     */
    @Subscribe
    public void historyAdd(ZKHistoryAddEvent event) {
        if (event.getItem() == this.item) {
            this.refresh();
        }
    }

    /**
     * 刷新历史
     */
    @FXML
    private void refresh() {
        if (!this.root.isSelected()) {
            return;
        }
        this.listTable.clearItems();
        if (this.item != null) {
            Map<String, Object> param = new HashMap<>();
            param.put("path", this.item.nodePath());
            param.put("infoId", this.item.info().getId());
            List<ZKDataHistory> histories = this.historyStore.list(param);
            this.listTable.addItem(ZKDataHistoryVO.convert(histories));
        }
    }

    /**
     * 执行初始化
     *
     * @param event
     */
    @Subscribe
    public void init(ZKHistoryShowEvent event) {
        this.item = event.data();
        this.refresh();
        this.root.selectTab();
    }

    @Override
    public String i18nId() {
        return "data.history";
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.saveTime.setCellValueFactory(new PropertyValueFactory<>("saveTimeExt"));
        this.dataSize.setCellValueFactory(new PropertyValueFactory<>("dataSize"));
        // 初始化表单
        this.initTable();
        this.root.selectedProperty().addListener((observable, oldValue, newValue) -> this.refresh());
    }
}
