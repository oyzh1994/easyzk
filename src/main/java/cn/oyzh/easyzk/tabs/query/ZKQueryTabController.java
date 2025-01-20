package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.dto.ZKQueryParam;
import cn.oyzh.easyzk.dto.ZKQueryResult;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKQueryStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextArea;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.i18n.I18nHelper;
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

    private ZKQuery query;

    private ZKClient zkClient;

    @FXML
    private RichDataTextArea content;

    @FXML
    private FlexTabPane resultTabPane;

    private final ZKQueryStore queryStore = ZKQueryStore.INSTANCE;

    public ZKConnect zkConnect() {
        return this.zkClient.zkConnect();
    }

    public void init(ZKClient client, ZKQuery query) {
        this.zkClient = client;
        if (query == null) {
            query = new ZKQuery();
            query.setIid(client.iid());
            query.setName(I18nHelper.unnamedQuery());
        } else {
            this.content.setText(query.getContent());
            this.content.setPromptText(null);
        }
        this.query = query;
    }

    @FXML
    private void save() {
        try {
            this.query.setContent(this.content.getText());
            if (this.query.getUid() == null) {
                String name = MessageBox.prompt(I18nHelper.pleaseInputName());
                if (StringUtil.isNotBlank(name)) {
                    this.queryStore.insert(this.query);
                    ZKEventUtil.queryAdded(this.query);
                }
            } else {
                this.queryStore.update(this.query);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void run() {
        try {
            ZKQueryParam param = new ZKQueryParam();
            param.setContent(this.content.getText());
            ZKQueryResult result = this.zkClient.query(param);
            this.content.flexHeight("50% - 70");
            this.resultTabPane.setVisible(true);
            this.resultTabPane.clearChild();
            if (param.isGet()) {
                this.resultTabPane.addTab(new ZKQueryMsgTab(param, result));
                if (result.isSuccess()) {
                    this.resultTabPane.addTab(new ZKQueryDataTab(param.getPath(), result.getData(), this.zkClient));
                    this.resultTabPane.select(1);
                } else {
                    this.resultTabPane.select(0);
                }
            }
            this.content.parentAutosize();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

}