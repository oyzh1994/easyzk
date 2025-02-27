package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import javafx.fxml.FXML;

import java.util.List;

/**
 * zk客户端stat信息tab内容组件
 *
 * @author oyzh
 * @since 2024/12/24
 */
public class ZKStatTabController extends SubTabController {

    /**
     * 状态信息
     */
    @FXML
    private FXTableView<ZKEnvNode> statTable;

    @Override
    public ZKServerTabController parent() {
        return (ZKServerTabController) super.parent();
    }

    @FXML
    private void refreshStat() {
        // 状态信息
        List<ZKEnvNode> statNodes = this.parent().client().statNodes();
        this.statTable.setItem(statNodes);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.getTab().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.refreshStat();
            }
        });
    }
}
