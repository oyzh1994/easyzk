package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import javafx.fxml.FXML;

import java.util.List;

/**
 * zk客户端本地信息tab内容组件
 *
 * @author oyzh
 * @since 2024/12/24
 */
public class ZKLocalTabController extends SubTabController {
    /**
     * 客户端环境
     */
    @FXML
    private FXTableView<ZKEnvNode> localEnvTable;

    @Override
    public ZKServerTabController parent() {
        return (ZKServerTabController) super.parent();
    }

    @FXML
    private void refreshLocal() {
        // 客户端环境信息
        List<ZKEnvNode> localEnviNodes = this.parent().getClient().localNodes();
        this.localEnvTable.setItem(localEnviNodes);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.getTab().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.refreshLocal();
            }
        });
    }
}
