package cn.oyzh.easyzk.controller;

import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.dto.ZKDataHistoryVO;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKHistoryAddedEvent;
import cn.oyzh.easyzk.event.ZKHistoryShowEvent;
import cn.oyzh.easyzk.event.ZKNodeSelectedEvent;
import cn.oyzh.easyzk.store.ZKDataHistoryStore2;
import cn.oyzh.easyzk.tabs.node.ZKConnectTab;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.plus.controller.SubStageController;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.TabClosedEvent;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.lang.ref.WeakReference;
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
     * zk树节点
     */
    private WeakReference<ZKNodeTreeItem> itemReference;

    /**
     * 存储对象
     */
    private final ZKDataHistoryStore2 historyStore = ZKDataHistoryStore2.INSTANCE;

    private ZKNodeTreeItem item() {
        return this.itemReference == null ? null : this.itemReference.get();
    }

    /**
     * tab关闭事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onTabClosed(TabClosedEvent event) {
        ZKNodeTreeItem item = this.item();
        if (item == null) {
            return;
        }
        if (event.data() instanceof ZKConnectTab tab1 && tab1.info() == item.info()) {
            this.itemReference = null;
            this.listTable.clearItems();
            this.root.disable();
        }
    }

    /**
     * 节点选中事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void onNodeSelected(ZKNodeSelectedEvent event) {
        if (event.data() == null) {
            this.itemReference = null;
            this.listTable.clearItems();
            this.root.disable();
            return;
        }
        this.root.enable();
        if (this.itemReference == null || event.data() != this.itemReference.get()) {
            this.itemReference = new WeakReference<>(event.data());
            this.refresh();
        }
    }

    /**
     * 历史新增事件
     */
    @EventSubscribe
    private void historyAdded(ZKHistoryAddedEvent event) {
        ZKNodeTreeItem item = this.item();
        if (event.item() == item) {
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
            ZKNodeTreeItem item = this.item();
            if (item == null) {
                return;
            }
            String path = item.nodePath();
            String infoId = item.info().getId();
            List<ZKDataHistory> histories;
            if (this.type.isSelected()) {
                histories = this.historyStore.listServer(infoId, path, item.client());
            } else {
                histories = this.historyStore.listLocal(infoId, path);
            }
            this.listTable.addItem(ZKDataHistoryVO.convert(histories));
        }
    }

    /**
     * 删除历史
     */
    @FXML
    private void delete() {
        ZKNodeTreeItem item = this.item();
        if (item == null) {
            return;
        }
        ZKDataHistory history = this.listTable.getSelectedItem();
        if (history == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            boolean result;
            if (this.type.isSelected()) {
                result = this.historyStore.deleteServer(history.getInfoId(), item.nodePath(), history.getSaveTime(), item.client());
            } else {
                result = this.historyStore.deleteLocal(history.getInfoId(), item.nodePath(), history.getSaveTime());
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
     */
    @FXML
    private void restore() {
        ZKNodeTreeItem item = this.item();
        if (item == null) {
            return;
        }
        ZKDataHistory history = this.listTable.getSelectedItem();
        if (history == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.restoreData())) {
            byte[] bytes;
            if (this.type.isSelected()) {
                bytes = this.historyStore.getServerData(history.getInfoId(), item.nodePath(), history.getSaveTime(), item.client());
            } else {
                bytes = this.historyStore.getLocalData(history.getInfoId(), item.nodePath(), history.getSaveTime());
            }
            if (bytes == null) {
                MessageBox.warn(I18nHelper.notFoundData());
            } else {
                ZKEventUtil.historyRestore(bytes, item);
            }
        }
    }

    /**
     * 显示历史
     *
     * @param event 事件
     */
    @EventSubscribe
    private void show(ZKHistoryShowEvent event) {
        this.itemReference = new WeakReference<>(event.data());
        this.root.selectTab();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 监听按钮
        this.root.selectedProperty().addListener((observable, oldValue, newValue) -> this.refresh());
        this.type.selectedProperty().addListener((observable, oldValue, newValue) -> this.refresh());
    }
}
