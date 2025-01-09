package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.dto.ZKClusterNode;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.fxml.FXML;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * zk客户端汇总信息tab内容组件
 *
 * @author oyzh
 * @since 2024/12/24
 */
public class ZKClusterController extends SubTabController {

    /**
     * zk客户端
     */
    @Setter
    @Accessors(fluent = true)
    private ZKClient zkClient;

    /**
     * 集群列表
     */
    @FXML
    private FlexTableView<ZKClusterNode> clusterTable;

    @FXML
    private void refreshCluster() {
        // 集群信息
        List<ZKClusterNode> clusterNodes = this.zkClient.clusterNodes();
        this.clusterTable.setItem(clusterNodes);
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        super.initialize(location, resourceBundle);
        this.getTab().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                this.refreshCluster();
            }
        })
    }
}
