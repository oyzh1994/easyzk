package cn.oyzh.easyzk.tabs.auth;

import cn.hutool.core.map.MapUtil;
import cn.oyzh.easyzk.controller.auth.ZKAuthAddController;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.dto.ZKAuthVO;
import cn.oyzh.easyzk.event.ZKAuthAddedEvent;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.controls.page.PageBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.table.FXTableCell;
import cn.oyzh.fx.plus.controls.table.FlexTableColumn;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.tabs.DynamicTabController;
import com.google.common.eventbus.Subscribe;
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
 * zk认证列表tab内容组件
 *
 * @author oyzh
 * @since 2023/11/03
 */
@Lazy
@Component
public class ZKAuthTabContent extends DynamicTabController {

    /**
     * 搜索词汇
     */
    @FXML
    private ClearableTextField searchKeyWord;

    /**
     * 分页组件
     */
    @FXML
    private PageBox<ZKAuth> pagePane;

    /**
     * 数据列表
     */
    @FXML
    private TableView<ZKAuthVO> listTable;

    /**
     * 数据索id列
     */
    @FXML
    private TableColumn<ZKAuthVO, String> index;

    /**
     * 用户名
     */
    @FXML
    private TableColumn<ZKAuthVO, String> user;

    /**
     * 密码
     */
    @FXML
    private TableColumn<ZKAuthVO, String> password;

    /**
     * 是否启用列
     */
    @FXML
    private FlexTableColumn<ZKAuthVO, String> enable;

    /**
     * 数据操作列
     */
    @FXML
    private FlexTableColumn<ZKAuthVO, String> action;

    /**
     * 分页数据
     */
    private Paging<ZKAuth> pageData;

    /**
     * 认证信息储存
     */
    private final ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    /**
     * 初始化数据列表
     *
     * @param pageNo 页码
     */
    private void initDataList(long pageNo) {
        this.pageData = this.authStore.getPage(20, MapUtil.of("searchKeyWord", this.searchKeyWord.getText()));
        this.listTable.getItems().clear();
        this.listTable.getItems().addAll(ZKAuthVO.convert(this.pageData.page(pageNo)));
        this.pagePane.setPaging(this.pageData);
    }

    /**
     * 初始化列表控件
     */
    private void initTable() {
        // 初始化操作栏
        this.action.setCellFactory((cell) -> new FXTableCell<>() {
            private HBox hBox;

            @Override
            public Node initGraphic() {
                if (this.hBox == null) {
                    // 删除按钮
                    SVGGlyph del = new SVGGlyph("/font/delete.svg", 14.d);
                    del.setTipText("删除");
                    del.setOnMousePrimaryClicked((event) -> delInfo(this.getTableItem()));
                    this.hBox = new HBox(del);
                    HBox.setMargin(del, new Insets(7, 0, 0, 5));
                }
                return this.hBox;
            }
        });

        // 状态栏初始化
        this.enable.setCellFactory((cell) -> new FXTableCell<>() {
            @Override
            public FXToggleSwitch initGraphic() {
                ZKAuthVO authVO = this.getTableItem();
                if (authVO != null) {
                    FXToggleSwitch toggleSwitch = new FXToggleSwitch();
                    toggleSwitch.setFontSize(11);
                    toggleSwitch.setSelectedText("已启用");
                    toggleSwitch.setUnselectedText("已禁用");
                    toggleSwitch.setSelected(authVO.getEnable());
                    toggleSwitch.selectedChanged((abs, o, n) -> {
                        authVO.setEnable(n);
                        if (authStore.update(authVO)) {
                            // ZKAuthUtil.fireAuthEnableEvent(authVO);
                            ZKEventUtil.authEnabled(authVO);
                        } else {
                            MessageBox.warn("修改状态失败！");
                        }
                    });
                    return toggleSwitch;
                }
                return null;
            }
        });
    }

    /**
     * 删除认证信息
     *
     * @param auth 认证信息
     */
    private void delInfo(ZKAuth auth) {
        if (MessageBox.confirm("确定删除此认证信息？")) {
            this.authStore.delete(auth);
            this.firstPage();
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
     * 添加zk认证信息
     */
    @FXML
    private void toAdd() {
        StageUtil.showStage(ZKAuthAddController.class);
    }

    /**
     * 认证新增事件
     */
    @Subscribe
    private void authAdded(ZKAuthAddedEvent event) {
        this.initDataList(Integer.MAX_VALUE);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        this.user.setCellValueFactory(new PropertyValueFactory<>("user"));
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.password.setCellValueFactory(new PropertyValueFactory<>("password"));
        this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
    }

    @Override
    public String i18nId() {
        return "auth.main";
    }
}
