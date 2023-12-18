package cn.oyzh.easyzk.controller.node;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKServerNode;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.PagePane;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.apache.zookeeper.Version;

import java.util.List;


/**
 * zk配置信息业务
 *
 * @author oyzh
 * @since 2022/08/25
 */
@StageAttribute(
        title = "zk服务信息",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        stageStyle = StageStyle.DECORATED,
        // cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "node/zkService.fxml"
)
public class ZKServiceController extends Controller {

    /**
     * sdk版本
     */
    @FXML
    private TextField sdkVersion;

    /**
     * zk连接地址
     */
    @FXML
    private TextArea zkInfoHost;

    /**
     * zk名称
     */
    @FXML
    private TextField zkInfoName;

    /**
     * 分页组件
     */
    @FXML
    private PagePane<ZKServerNode> pagePane;

    /**
     * 数据列表
     */
    @FXML
    private TableView<ZKServerNode> listTable;

    /**
     * id列
     */
    @FXML
    private TableColumn<ZKServerNode, Long> id;

    /**
     * 交互地址列
     */
    @FXML
    private TableColumn<ZKServerNode, String> addr;

    /**
     * 类型列
     */
    @FXML
    private TableColumn<ZKServerNode, String> type;

    /**
     * 权重列
     */
    @FXML
    private TableColumn<ZKServerNode, Long> weight;

    /**
     * 客户端连接地址列
     */
    @FXML
    private TableColumn<ZKServerNode, String> clientAddr;

    /**
     * 选举地址列
     */
    @FXML
    private TableColumn<ZKServerNode, String> electionAddr;

    /**
     * 分页数据
     */
    private Paging<ZKServerNode> pageData;

    /**
     * 上一页
     */
    @FXML
    private void prevPage() {
        this.listTable.getItems().clear();
        this.listTable.getItems().addAll(this.pageData.prev());
    }

    /**
     * 下一页
     */
    @FXML
    private void nextPage() {
        this.listTable.getItems().clear();
        this.listTable.getItems().addAll(this.pageData.next());
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.id.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.addr.setCellValueFactory(new PropertyValueFactory<>("addr"));
        this.type.setCellValueFactory(new PropertyValueFactory<>("type"));
        this.weight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        this.clientAddr.setCellValueFactory(new PropertyValueFactory<>("clientAddr"));
        this.electionAddr.setCellValueFactory(new PropertyValueFactory<>("electionAddr"));

        ZKInfo zkInfo = this.getStageProp("zkInfo");
        this.zkInfoName.setText(zkInfo.getName());
        this.zkInfoHost.setText(zkInfo.getHost());
        this.sdkVersion.setText(Version.getFullVersion());

        ZKClient zkClient = this.getStageProp("zkClient");
        List<ZKServerNode> servers = zkClient.getServers();
        this.pageData = new Paging<>(servers, 10);
        this.pagePane.setPaging(this.pageData);
        this.listTable.getItems().clear();
        this.listTable.getItems().addAll(this.pageData.first());
        this.stage.hideOnEscape();
    }
}
