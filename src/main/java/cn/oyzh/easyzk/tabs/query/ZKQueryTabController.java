package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.dto.ZKQueryParam;
import cn.oyzh.easyzk.dto.ZKQueryResult;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.fx.ZKQueryTextArea;
import cn.oyzh.easyzk.store.ZKQueryStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import lombok.Getter;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryTabController extends DynamicTabController {

    @Getter
    private ZKQuery query;

    /**
     * 未保存标志位
     */
    @Getter
    private boolean unsaved;

    private ZKClient zkClient;

    @FXML
    private ZKQueryTextArea content;

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
            this.unsaved = true;
        } else {
            this.content.setText(query.getContent());
            this.content.setPromptText(null);
        }
        this.content.addTextChangeListener((observable, oldValue, newValue) -> {
            this.unsaved = true;
            this.flushTab();
        });
        this.query = query;
    }

    @FXML
    private void save() {
        try {
            this.query.setContent(this.content.getText());
            if (this.query.getUid() == null) {
                String name = MessageBox.prompt(I18nHelper.pleaseInputName());
                if (StringUtil.isNotBlank(name)) {
                    this.query.setName(name);
                    this.queryStore.insert(this.query);
                    ZKEventUtil.queryAdded(this.query);
                }
            } else {
                this.queryStore.update(this.query);
            }
            this.unsaved = false;
            this.flushTab();
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
            this.content.flexHeight("30% - 70");
            this.resultTabPane.setVisible(true);
            this.resultTabPane.clearChild();
            this.resultTabPane.addTab(new ZKQueryMsgTab(param, result));
            if (param.isGet()) {
                if (result.isSuccess()) {
                    this.resultTabPane.addTab(new ZKQueryDataTab(param.getPath(), result.getData(), this.zkClient));
                    if (param.hasParamStat()) {
                        this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                    }
                    this.resultTabPane.select(1);
                } else {
                    this.resultTabPane.select(0);
                }
            } else if (param.isLs() || param.isLs2()) {
                if (result.isSuccess()) {
                    this.resultTabPane.addTab(new ZKQueryNodeTab(param.getPath(), result.getNodes()));
                    if (param.hasParamStat()) {
                        this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                    }
                    this.resultTabPane.select(1);
                } else {
                    this.resultTabPane.select(0);
                }
            } else if (param.isSet()) {
                if (result.isSuccess() && param.hasParamStat()) {
                    this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                }
                this.resultTabPane.select(0);
            } else if (param.isGetACL()) {
                if (result.isSuccess()) {
                    this.resultTabPane.addTab(new ZKQueryACLTab(result.getACLList()));
                    if (param.hasParamStat()) {
                        this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                    }
                    this.resultTabPane.select(1);
                } else {
                    this.resultTabPane.select(0);
                }
            } else if (param.isCreate()) {
                this.resultTabPane.select(0);
            } else if (param.isSync()) {
                this.resultTabPane.select(0);
            }
            this.content.parentAutosize();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @FXML
    private void onContentKeyPressed(KeyEvent event) {
        if (KeyboardUtil.isCtrlS(event)) {
            this.save();
        } else if (KeyboardUtil.isCtrlR(event)) {
            this.run();
        }
    }

    @Override
    public void onCloseRequest(DynamicTab tab, Event event) {
        if (this.unsaved && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            event.consume();
        } else {
            super.onCloseRequest(tab, event);
        }
    }
}