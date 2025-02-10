package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import javafx.fxml.FXML;

import java.util.List;

/**
 * zk客户端srvr信息tab内容组件
 *
 * @author oyzh
 * @since 2024/12/24
 */
public class ZKSrvrTabController extends SubTabController {

    /**
     * 服务信息
     */
    @FXML
    private FlexTableView<ZKEnvNode> srvrTable;

    @Override
    public ZKServerTabController parent() {
        return (ZKServerTabController) super.parent();
    }

    @FXML
    private void refreshSrvr() {
        // 服务信息
        List<ZKEnvNode> srvrNodes = this.parent().client().srvrNodes();
        this.srvrTable.setItem(srvrNodes);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.getTab().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.refreshSrvr();
            }
        });
    }
}
