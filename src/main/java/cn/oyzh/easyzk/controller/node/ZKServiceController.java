package cn.oyzh.easyzk.controller.node;

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
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * zk配置信息业务
 *
 * @author oyzh
 * @since 2022/08/25
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        stageStyle = StageStyle.DECORATED,
        value = ZKConst.FXML_BASE_PATH + "node/zkService.fxml"
)
public class ZKServiceController extends StageController {

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

        // 客户端信息
        List<ZKEnvNode> localEnviNodes = zkClient.localEnviNodes();
        this.localEnvTable.setItem(localEnviNodes);

        // 服务信息
        List<ZKEnvNode> serverEnviNodes = zkClient.serverEnviNodes();
        this.serverEnvTable.setItem(serverEnviNodes);

        // 集群信息
        List<ZKClusterNode> clusterNodes = zkClient.getServers();
        this.clusterTable.setItem(clusterNodes);
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.serverInfo();
    }
}
