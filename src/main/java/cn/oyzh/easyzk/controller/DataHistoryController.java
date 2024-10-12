package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.dto.ZKDataHistoryVO;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKHistoryAddedEvent;
import cn.oyzh.easyzk.event.ZKHistoryShowEvent;
import cn.oyzh.easyzk.event.ZKTabChangedEvent;
import cn.oyzh.easyzk.store.ZKDataHistoryStore2;
// import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.tabs.node.ZKConnectTab;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.svg.DeleteSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.UndoSVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.table.GraphicTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nHelper;
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
import java.util.List;
import java.util.ResourceBundle;


/**
 * zk数据历史业务
 *
 * @author oyzh
 * @since 2024/04/23
 */
public class DataHistoryController extends SubStageController implements Initializable {

    /**
     * 根节点
     */
    @FXML
    private FXTab root;

    /**
     * 类型
     */
    @FXML
    private FXToggleSwitch type;

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
     * 保存时间列
     */
    @FXML
    private TableColumn<ZKDataHistoryVO, String> saveTime;

    /**
     * 数据大小列
     */
    @FXML
    private TableColumn<ZKDataHistoryVO, String> dataSize;

    /**
     * 数据操作列
     */
    @FXML
    private FlexTableColumn<ZKDataHistoryVO, String> action;

    /**
     * zk树节点
     */
    private ZKNodeTreeItem item;

    /**
     * 初始化列表控件
     */
    private void initTable() {
        // 操作栏初始化
        this.action.setCellFactory((cell) -> new GraphicTableCell<>() {
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
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            boolean result;
            if (this.type.isSelected()) {
                result = ZKDataHistoryStore2.INSTANCE.deleteServer(history.getInfoId(), this.item.nodePath(), history.getSaveTime(), this.item.client());
            } else {
                result = ZKDataHistoryStore2.INSTANCE.deleteLocal(history.getInfoId(), this.item.nodePath(), history.getSaveTime());
            }
            if (result) {
                this.refresh();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    /**
     * 恢复历史
     *
     * @param history 历史记录
     */
    private void restore(ZKDataHistory history) {
        if (MessageBox.confirm(I18nHelper.restoreData())) {
            byte[] bytes;
            if (this.type.isSelected()) {
                bytes = ZKDataHistoryStore2.INSTANCE.getServerData(history.getInfoId(), this.item.nodePath(), history.getSaveTime(), this.item.client());
            } else {
                bytes = ZKDataHistoryStore2.INSTANCE.getLocalData(history.getInfoId(), this.item.nodePath(), history.getSaveTime());
            }
            if (bytes == null) {
                MessageBox.warn(I18nHelper.notFoundData());
            } else {
                ZKEventUtil.historyRestore(bytes, this.item);
            }
        }
    }

    /**
     * tab变化事件
     */
    @Subscribe
    public void tabChanged(ZKTabChangedEvent event) {
        Tab tab = event.data();
        if (tab instanceof ZKConnectTab tab1) {
            if (tab1.activeItem() != this.item) {
                this.item = tab1.activeItem();
                this.refresh();
            }
        } else {
            this.item = null;
        }
    }

    /**
     * 历史新增事件
     */
    @Subscribe
    public void historyAdded(ZKHistoryAddedEvent event) {
        if (event.item() == this.item) {
            this.refresh();
        }
    }

    /**
     * 刷新历史
     */
    @FXML
    private void refresh() {
        if (this.root.isSelected()) {
            this.listTable.clearItems();
            if (this.item != null) {
                String path = this.item.nodePath();
                String infoId = this.item.info().getId();
                List<ZKDataHistory> histories;
                if (this.type.isSelected()) {
                    histories = ZKDataHistoryStore2.INSTANCE.listServer(infoId, path, this.item.client());
                } else {
                    histories = ZKDataHistoryStore2.INSTANCE.listLocal(infoId, path);
                }
                this.listTable.addItem(ZKDataHistoryVO.convert(histories));
            }
        }
    }

    /**
     * 显示历史
     *
     * @param event 事件
     */
    @Subscribe
    public void show(ZKHistoryShowEvent event) {
        this.item = event.data();
        this.root.selectTab();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.dataSize.setCellValueFactory(new PropertyValueFactory<>("dataSize"));
        this.saveTime.setCellValueFactory(new PropertyValueFactory<>("saveTimeFormated"));
        // 初始化表单
        this.initTable();
        // 监听按钮
        this.root.selectedProperty().addListener((observable, oldValue, newValue) -> this.refresh());
        this.type.selectedProperty().addListener((observable, oldValue, newValue) -> this.refresh());
    }
}
