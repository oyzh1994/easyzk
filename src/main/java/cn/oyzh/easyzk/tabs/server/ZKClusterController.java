package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.dto.ZKClusterNode;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.fxml.FXML;

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
     * 集群列表
     */
    @FXML
    private FlexTableView<ZKClusterNode> clusterTable;

    @Override
    public ZKServerTabController parent() {
        return (ZKServerTabController) super.parent();
    }

    @FXML
    private void refreshCluster() {
        // 集群信息
        List<ZKClusterNode> clusterNodes = this.parent().client().clusterNodes();
        this.clusterTable.setItem(clusterNodes);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.getTab().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.refreshCluster();
            }
        });
    }
}
