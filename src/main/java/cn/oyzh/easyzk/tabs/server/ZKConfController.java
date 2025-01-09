package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.dto.ZKEnvNode;
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
public class ZKConfController extends SubTabController {

    /**
     * 配置信息
     */
    @FXML
    private FlexTableView<ZKEnvNode> confTable;

    @Override
    public ZKServerTabController parent() {
        return (ZKServerTabController) super.parent();
    }

    @FXML
    private void refreshConf() {
        // 配置信息
        List<ZKEnvNode> confNodes = this.parent().client().confNodes();
        this.confTable.setItem(confNodes);
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.getTab().selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                this.refreshConf();
            }
        });
    }
}
