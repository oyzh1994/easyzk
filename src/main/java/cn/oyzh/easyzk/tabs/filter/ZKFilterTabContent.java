package cn.oyzh.easyzk.tabs.filter;

import cn.oyzh.easyzk.controller.filter.ZKFilterAddController;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.dto.ZKFilterVO;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKFilterAddedEvent;
import cn.oyzh.easyzk.store.ZKFilterStore2;
import cn.oyzh.common.dto.Paging;
import cn.oyzh.fx.plus.controls.page.PageBox;
import cn.oyzh.fx.plus.controls.svg.DeleteSVGGlyph;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.table.GraphicTableCell;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.toggle.EnabledToggleSwitch;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.controls.toggle.MatchToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import cn.oyzh.fx.plus.window.StageManager;
import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * zk过滤列表tab内容组件
 *
 * @author oyzh
 * @since 2023/11/03
 */
public class ZKFilterTabContent extends DynamicTabController {

    /**
     * 分页组件
     */
    @FXML
    private PageBox<ZKFilter> pagePane;

    /**
     * 搜索词汇
     */
    @FXML
    private ClearableTextField searchKeyWord;

    /**
     * 数据列表
     */
    @FXML
    private FlexTableView<ZKFilter> listTable;

    /**
     * 分页数据
     */
    private Paging<ZKFilter> pageData;

    /**
     * zk过滤配置储存
     */
    private final ZKFilterStore2 filterStore = ZKFilterStore2.INSTANCE;

    /**
     * 初始化数据列表
     *
     * @param pageNo 数据页码
     */
    private void initDataList(long pageNo) {
        if (this.pageData != null) {
            pageNo = this.pageData.fixPageNo(pageNo);
        }
        this.pageData = this.filterStore.getPage(pageNo, 20, this.searchKeyWord.getText());
        this.listTable.setItem(ZKFilterVO.convert(this.pageData.dataList()));
        this.pagePane.setPaging(this.pageData);
    }


    /**
     * 添加过滤
     */
    @FXML
    private void add() {
        StageManager.showStage(ZKFilterAddController.class);
    }

    /**
     * 删除过滤
     */
    @FXML
    private void delete() {
        ZKFilter filter = this.listTable.getSelectedItem();
        if (filter == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            if (this.filterStore.delete(filter.getKw())) {
                ZKEventUtil.treeChildFilter();
                this.listTable.removeItem(filter);
                if (this.listTable.isItemEmpty()) {
                    this.firstPage();
                }
            } else {
                MessageBox.warn(I18nHelper.operationFail());
            }
        }
    }

    /**
     * 首页
     */
    private void firstPage() {
        this.initDataList(0);
    }

    /**
     * 上一页
     */
    @FXML
    private void prevPage() {
        this.initDataList(this.pageData.currentPage() - 1);
    }

    /**
     * 下一页
     */
    @FXML
    private void nextPage() {
        this.initDataList(this.pageData.currentPage() + 1);
    }

    /**
     * 过滤新增事件
     */
    @Subscribe
    private void filterAdded(ZKFilterAddedEvent event) {
        this.initDataList(this.pageData.currentPage());
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        super.initialize(url, resources);
        // 显示首页
        this.firstPage();
    }
}
