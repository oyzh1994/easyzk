package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.dto.ZKQueryParam;
import cn.oyzh.easyzk.dto.ZKQueryResult;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextArea;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * zk更新日志tab内容组件
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ZKQueryTabController extends DynamicTabController {

    private ZKClient zkClient;

    @FXML
    private RichDataTextArea content;

    @FXML
    private FlexTabPane resultTabPane;

    public ZKConnect zkConnect() {
        return this.zkClient.zkConnect();
    }

    public void init(ZKClient client) {
        this.zkClient = client;
    }

    @FXML
    private void save() {
    }

    @FXML
    private void run() {
        ZKQueryParam param = new ZKQueryParam();
        param.setContent(content.getText());
        ZKQueryResult result = this.zkClient.query(param);
        this.content.flexHeight("50% - 70");
        this.resultTabPane.setVisible(true);
        this.resultTabPane.clearChild();
        if (param.isGet()) {
            this.resultTabPane.addTab(new ZKQueryMsgTab(param, result));
        }
        this.content.parentAutosize();
    }

}