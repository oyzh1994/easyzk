package cn.oyzh.easyzk.tabs.filter;

import cn.hutool.core.map.MapUtil;
import cn.oyzh.easyzk.controller.filter.ZKFilterAddController;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.dto.ZKFilterVO;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKFilterStore;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controls.FXToggleSwitch;
import cn.oyzh.fx.plus.controls.page.PageBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * zk过滤列表tab内容组件
 *
 * @author oyzh
 * @since 2023/11/03
 */
@Lazy
@Component
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
    private TableView<ZKFilter> listTable;

    /**
     * 数据索id列
     */
    @FXML
    private TableColumn<ZKFilterVO, String> index;

    /**
     * 关键词列
     */
    @FXML
    private TableColumn<ZKFilterVO, String> kw;

    /**
     * 数据状态列
     */
    @FXML
    private FlexTableColumn<ZKFilterVO, String> enable;

    /**
     * 数据名称列
     */
    @FXML
    private FlexTableColumn<ZKFilterVO, String> partMatch;

    /**
     * 数据操作列
     */
    @FXML
    private FlexTableColumn<ZKFilterVO, String> action;

    /**
     * 分页数据
     */
    private Paging<ZKFilter> pageData;

    /**
     * zk过滤配置储存
     */
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * 初始化数据列表
     *
     * @param pageNo 数据页码
     */
    private void initDataList(long pageNo) {
        this.pageData = this.filterStore.getPage(20, MapUtil.of("searchKeyWord", this.searchKeyWord.getText()));
        this.listTable.getItems().clear();
        this.listTable.getItems().addAll(ZKFilterVO.convert(this.pageData.page(pageNo)));
        this.pagePane.setPaging(this.pageData);
    }

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
                    SVGGlyph del = new SVGGlyph("/font/delete.svg", 14.d);
                    del.setTipText("删除");
                    del.setOnMousePrimaryClicked((event) -> deleteInfo(this.getTableItem()));
                    this.hBox = new HBox(del);
                    HBox.setMargin(del, new Insets(7, 0, 0, 5));
                }
                return hBox;
            }
        });

        // 状态栏初始化
        this.enable.setCellFactory((cell) -> new FXTableCell<>() {
            @Override
            public FXToggleSwitch initGraphic() {
                ZKFilterVO filterVO = this.getTableItem();
                if (filterVO != null) {
                    FXToggleSwitch toggleSwitch = new FXToggleSwitch();
                    toggleSwitch.setFontSize(11);
                    toggleSwitch.setSelectedText("已启用");
                    toggleSwitch.setUnselectedText("已禁用");
                    toggleSwitch.setSelected(filterVO.isEnable());
                    toggleSwitch.selectedChanged((abs, o, n) -> {
                        filterVO.setEnable(n);
                        if (!filterStore.update(filterVO)) {
                            MessageBox.warn("修改状态失败！");
                        } else {
                            ZKEventUtil.treeChildFilter();
                        }
                    });
                    return toggleSwitch;
                }
                return null;
            }
        });

        // 匹配模式栏初始化
        this.partMatch.setCellFactory((cell) -> new FXTableCell<>() {
            @Override
            public FXToggleSwitch initGraphic() {
                ZKFilterVO filterVO = this.getTableItem();
                if (filterVO != null) {
                    FXToggleSwitch toggleSwitch = new FXToggleSwitch();
                    toggleSwitch.fontSize(11);
                    toggleSwitch.setSelectedText("模糊匹配");
                    toggleSwitch.setUnselectedText("完全匹配");
                    toggleSwitch.setSelected(filterVO.isPartMatch());
                    toggleSwitch.selectedChanged((obs, o, n) -> {
                        filterVO.setPartMatch(n);
                        if (!filterStore.update(filterVO)) {
                            MessageBox.warn("修改匹配方式失败！");
                        } else if (filterVO.isEnable()) {
                            ZKEventUtil.treeChildFilter();
                        }
                    });
                    return toggleSwitch;
                }
                return null;
            }
        });
    }

    /**
     * 删除zk信息
     *
     * @param info zk信息
     */
    private void deleteInfo(ZKFilter info) {
        if (MessageBox.confirm("确定删除此过滤配置？")) {
            if (this.filterStore.delete(info)) {
                ZKEventUtil.treeChildFilter();
                this.firstPage();
            } else {
                MessageBox.warn("删除过滤配置失败！");
            }
        }
    }

    /**
     * 添加zk信息
     */
    @FXML
    private void toAdd() {
        StageUtil.showStage(ZKFilterAddController.class);
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
    @EventReceiver(ZKEventTypes.ZK_FILTER_ADDED)
    private void filterAdded() {
        this.initDataList(Integer.MAX_VALUE);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // 注册事件处理
        EventUtil.register(this);
        this.kw.setCellValueFactory(new PropertyValueFactory<>("kw"));
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
    }

    @Override
    public void onTabClose(Event event) {
        // 取消注册事件处理
        EventUtil.unregister(this);
    }
}
