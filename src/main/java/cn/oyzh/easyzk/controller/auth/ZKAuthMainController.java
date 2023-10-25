package cn.oyzh.easyzk.controller.auth;

import cn.hutool.core.map.MapUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.dto.ZKAuthVO;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FXTableCell;
import cn.oyzh.fx.plus.controls.PagePane;
import cn.oyzh.fx.plus.controls.ToggleSwitch;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.ext.ClearableTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * zk认证信息业务
 *
 * @author oyzh
 * @since 2020/9/14
 */
@Slf4j
@StageAttribute(
        title = "zk认证信息列表",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "auth/zkAuthMain.fxml"
)
public class ZKAuthMainController extends Controller {

    /**
     * 搜索词汇
     */
    @FXML
    private ClearableTextField searchKeyWord;

    /**
     * 分页组件
     */
    @FXML
    private PagePane<ZKAuth> pagePane;

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
    private TableColumn<ZKAuthVO, String> enable;

    /**
     * 数据操作列
     */
    @FXML
    private TableColumn<ZKAuthVO, String> action;

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
    private void initDataList(int pageNo) {
        this.pageData = this.authStore.getPage(10, MapUtil.of("searchKeyWord", this.searchKeyWord.getText()));
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
                    this.hBox.setSpacing(5);
                }
                return this.hBox;
            }
        });

        // 状态栏初始化
        this.enable.setCellFactory((cell) -> new FXTableCell<>() {
            @Override
            public ToggleSwitch initGraphic() {
                ZKAuthVO authVO = this.getTableItem();
                if (authVO != null) {
                    ToggleSwitch toggleSwitch = new ToggleSwitch();
                    toggleSwitch.setStyle("-fx-font-size: 11");
                    toggleSwitch.setPrefWidth(65);
                    toggleSwitch.setSelectedText("已启用");
                    toggleSwitch.setUnselectedText("已禁用");
                    toggleSwitch.setSelected(authVO.getEnable());
                    toggleSwitch.selectedChanged((abs, o, n) -> {
                        authVO.setEnable(n);
                        if (authStore.update(authVO)) {
                            ZKAuthUtil.fireAuthEnableEvent(authVO);
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
        StageUtil.showStage(ZKAuthAddController.class, this.stage);
    }

    @Override
    public void onStageShown(@NonNull WindowEvent event) {
        // 注册事件处理
        EventUtil.register(this);
        this.user.setCellValueFactory(new PropertyValueFactory<>("user"));
        this.index.setCellValueFactory(new PropertyValueFactory<>("index"));
        this.password.setCellValueFactory(new PropertyValueFactory<>("password"));
        this.searchKeyWord.addTextChangeListener((observableValue, s, t1) -> this.firstPage());
        // 初始化表单
        this.initTable();
        // 显示首页
        this.firstPage();
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        // 取消注册事件处理
        EventUtil.unregister(this);
    }

    /**
     * 认证新增事件
     */
    @EventReceiver(ZKEventTypes.ZK_AUTH_ADDED)
    private void authAdded() {
        this.initDataList(Integer.MAX_VALUE);
    }
}
