package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.query.ZKQueryParam;
import cn.oyzh.easyzk.query.ZKQueryResult;
import cn.oyzh.easyzk.query.ZKQueryEditorPane;
import cn.oyzh.easyzk.store.ZKQueryStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryTabController extends RichTabController {

    /**
     * 查询对象
     */
    private ZKQuery query;

    public ZKQuery getQuery() {
        return query;
    }

    /**
     * 未保存标志位
     */
    private boolean unsaved;

    public boolean isUnsaved() {
        return unsaved;
    }

    /**
     * zk客户端
     */
    private ZKClient zkClient;

    /**
     * 当前内容
     */
    @FXML
    private ZKQueryEditorPane content;

    /**
     * 结果面板
     */
    @FXML
    private FXTabPane resultTabPane;

    /**
     * 查询存储
     */
    private final ZKQueryStore queryStore = ZKQueryStore.INSTANCE;

    public ZKConnect zkConnect() {
        return this.zkClient.zkConnect();
    }

    public void init(ZKClient client, ZKQuery query) {
        this.zkClient = client;
        this.content.setClient(client);
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
        StageManager.showMask(() -> {
            try {
//                this.disableTab();
                ZKQueryParam param = new ZKQueryParam();
                param.setContent(this.content.getText());
                ZKQueryResult result = this.zkClient.query(param);
                this.content.flexHeight("30% - 60");
                this.resultTabPane.setVisible(true);
                this.resultTabPane.clearChild();
                this.resultTabPane.addTab(new ZKQueryMsgTab(param, result));
                if (param.isGet()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryDataTab(param.getPath(), result.asData(), this.zkClient));
                        if (param.hasParamStat()) {
                            this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                        }
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                } else if (param.isLs() || param.isLs2()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryNodeTab(param.getPath(), result.asNode()));
                        if (param.hasParamStat()) {
                            this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                        }
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                } else if (param.isGetEphemerals()) {
                    if (result.isSuccess()) {
                        String path = param.getPath() == null ? "/" : param.getPath();
                        this.resultTabPane.addTab(new ZKQueryNodeTab(path, result.asNode()));
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
//            } else if (param.isGetAllChildrenNumber()) {
//                if (result.isSuccess()) {
//                    this.resultTabPane.addTab(new ZKQueryCountTab(result.asCount()));
//                    this.resultTabPane.select(1);
//                } else {
//                    this.resultTabPane.select(0);
//                }
                } else if (param.isWhoami()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryWhoamiTab(result.asClientInfo()));
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                } else if (param.isSrvr() || param.isEnvi() || param.isMntr() || param.isConf() || param.isStat4()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryEnvTab(result.asEnvInfo()));
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                } else if (param.isSet() || param.isSetACL()) {
                    if (result.isSuccess() && param.hasParamStat()) {
                        this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                    }
                    this.resultTabPane.select(0);
                } else if (param.isGetACL()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryACLTab(result.asACL()));
                        if (param.hasParamStat()) {
                            this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                        }
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                } else if (param.isCreate() || param.isSync() || param.isSetQuota() || param.isRmr() || param.isDeleteall()
                        || param.isDelete()) {
                    this.resultTabPane.select(0);
                } else if (param.isStat()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryStatTab(result.getStat()));
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                } else if (param.isListquota()) {
                    if (result.isSuccess()) {
                        this.resultTabPane.addTab(new ZKQueryQuotaTab(result.asQuota()));
                        this.resultTabPane.select(1);
                    } else {
                        this.resultTabPane.select(0);
                    }
                }
                this.content.parentAutosize();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
//            } finally {
//                this.enableTab();
            }
        });
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
    public void onTabCloseRequest(Event event) {
        if (this.unsaved && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            event.consume();
        } else {
            super.onTabCloseRequest(event);
        }
    }
}