package cn.oyzh.easyzk.controller.connect;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.easyzk.dto.ZKClusterNode;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * zk配置信息业务
 *
 * @author oyzh
 * @since 2022/08/25
 */
@StageAttribute(
        iconUrl = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "connect/zkServerInfo.fxml"
)
public class ZKServerInfoController extends StageController {

    /**
     * 状态信息
     */
    @FXML
    private FlexTableView<ZKEnvNode> statTable;

    /**
     * 配置信息
     */
    @FXML
    private FlexTableView<ZKEnvNode> confTable;

    /**
     * 服务信息
     */
    @FXML
    private FlexTableView<ZKEnvNode> srvrTable;

    /**
     * 客户端环境
     */
    @FXML
    private FlexTableView<ZKEnvNode> localEnvTable;

    /**
     * 服务端环境
     */
    @FXML
    private FlexTableView<ZKEnvNode> serverEnvTable;

    /**
     * 集群列表
     */
    @FXML
    private FlexTableView<ZKClusterNode> clusterTable;

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageShown(WindowEvent event) {
        ZKClient zkClient = this.getWindowProp("zkClient");

        // 客户端环境信息
        List<ZKEnvNode> localEnviNodes = zkClient.localEnvNodes();
        this.localEnvTable.setItem(localEnviNodes);

        // 服务端环境信息
        List<ZKEnvNode> serverEnviNodes = zkClient.serverEnvNodes();
        this.serverEnvTable.setItem(serverEnviNodes);

        // 服务信息
        List<ZKEnvNode> srvrNodes = zkClient.srvrNodes();
        this.srvrTable.setItem(srvrNodes);

        // 状态信息
        List<ZKEnvNode> statNodes = zkClient.statNodes();
        this.statTable.setItem(statNodes);

        // 配置信息
        List<ZKEnvNode> confNodes = zkClient.confNodes();
        this.confTable.setItem(confNodes);

        // 集群信息
        List<ZKClusterNode> clusterNodes = zkClient.clusterNodes();
        this.clusterTable.setItem(clusterNodes);
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.serverInfo();
    }
}
