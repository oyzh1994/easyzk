package cn.oyzh.easyzk.controller.data;

import cn.oyzh.common.thread.DownLatch;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.SystemUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.fx.ZKConnectComboBox;
import cn.oyzh.easyzk.handler.ZKDataTransportHandler;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKClientUtil;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.gui.combobox.CharsetComboBox;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.label.FlexLabel;
import cn.oyzh.fx.gui.text.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleGroup;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * db数据传输业务
 *
 * @author oyzh
 * @since 2024/09/05
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.VIEW_PATH + "data/zkDataTransport.fxml"
)
public class ZKDataTransportController extends StageController {

    /**
     * 第一步
     */
    @FXML
    private FlexVBox step1;

    /**
     * 第二步
     */
    @FXML
    private FlexVBox step2;

    /**
     * 第三步
     */
    @FXML
    private FlexVBox step3;

    /**
     * 来源信息名称
     */
    @FXML
    private FXLabel sourceInfoName;

    /**
     * 目标信息名称
     */
    @FXML
    private FXLabel targetInfoName;

    /**
     * 来源信息
     */
    @FXML
    private ZKConnectComboBox sourceInfo;

    /**
     * 来源字符集
     */
    @FXML
    private CharsetComboBox sourceCharset;

    /**
     * 来源字符集名称
     */
    @FXML
    private FlexLabel sourceCharsetName;

    /**
     * 目标信息
     */
    @FXML
    private ZKConnectComboBox targetInfo;

    /**
     * 目标字符集
     */
    @FXML
    private CharsetComboBox targetCharset;

    /**
     * 目标字符集名称
     */
    @FXML
    private FlexLabel targetCharsetName;

    /**
     * 来源主机
     */
    @FXML
    private FlexLabel sourceHost;

    /**
     * 目标主机
     */
    @FXML
    private FlexLabel targetHost;

    /**
     * 来源客户端
     */
    private ZKClient sourceClient;

    /**
     * 目标客户端
     */
    private ZKClient targetClient;

    /**
     * 结束传输按钮
     */
    @FXML
    private FlexButton stopTransportBtn;

    /**
     * 传输状态
     */
    @FXML
    private FXLabel transportStatus;

    /**
     * 传输消息
     */
    @FXML
    private MsgTextArea transportMsg;

    /**
     * 节点存在时处理策略
     */
    @FXML
    private FXToggleGroup existsPolicy;

    /**
     * 适用过滤配置
     */
    @FXML
    private FXCheckBox applyFilter;

    /**
     * 传输操作任务
     */
    private Thread execTask;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 传输处理器
     */
    private ZKDataTransportHandler transportHandler;

    /**
     * 过滤配置储存
     */
    private final ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    /**
     * 执行传输
     */
    @FXML
    private void doTransport() {
        // 重置参数
        this.counter.reset();
        // 清理信息
        this.transportMsg.clear();
        this.transportStatus.clear();
        // 生成传输处理器
        if (this.transportHandler == null) {
            this.transportHandler = new ZKDataTransportHandler();
            this.transportHandler
                    .messageHandler(str -> this.transportMsg.appendLine(str))
                    .processedHandler(count -> {
                        if (count == 0) {
                            this.counter.updateIgnore();
                        } else if (count < 0) {
                            this.counter.incrFail(count);
                        } else {
                            this.counter.incrSuccess(count);
                        }
                        this.updateStatus(I18nHelper.transportInProgress());
                    });
        } else {
            this.transportHandler.interrupt(false);
        }
        // 来源客户端
        this.transportHandler.sourceClient(this.sourceClient);
        // 目标客户端
        this.transportHandler.targetClient(this.targetClient);
        // 来源字符集
        this.transportHandler.sourceCharset(this.sourceCharset.getCharset());
        // 目标字符集
        this.transportHandler.targetCharset(this.targetCharset.getCharset());
        // 节点存在时处理策略
        this.transportHandler.existsPolicy(this.existsPolicy.selectedUserData());
        // 适用过滤
        if (this.applyFilter.isSelected()) {
            this.transportHandler.filters(this.filterStore.loadEnable());
        } else {
            this.transportHandler.filters(null);
        }
        // 开始处理
        NodeGroupUtil.disable(this.stage, "exec");
        this.stage.appendTitle("===" + I18nHelper.transportInProgress() + "===");
        // 执行传输
        this.execTask = ThreadUtil.start(() -> {
            try {
                this.stopTransportBtn.enable();
                // 更新状态
                this.updateStatus(I18nHelper.transportStarting());
                // 执行传输
                this.transportHandler.doTransport();
                // 更新状态
                this.updateStatus(I18nHelper.transportFinished());
            } catch (Exception ex) {
                if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus(I18nHelper.operationCancel());
                } else {
                    ex.printStackTrace();
                    this.updateStatus(I18nHelper.operationFail());
                }
            } finally {
                // 结束处理
                NodeGroupUtil.enable(this.stage, "exec");
                this.stopTransportBtn.disable();
                this.stage.restoreTitle();
                SystemUtil.gcLater();
            }
        });
    }

    /**
     * 结束传输
     */
    @FXML
    private void stopTransport() {
        ThreadUtil.interrupt(this.execTask);
        this.execTask = null;
        if (this.transportHandler != null) {
            this.transportHandler.interrupt();
        }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.sourceInfo.selectedItemChanged((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.sourceHost.setText(newValue.getHost());
                this.sourceInfoName.setText(newValue.getName());
            } else {
                this.sourceHost.clear();
                this.sourceInfoName.clear();
            }
            if (this.sourceClient != null) {
                this.sourceClient.close();
                this.sourceClient = null;
            }
        });
        this.targetInfo.selectedItemChanged((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.targetHost.setText(newValue.getHost());
                this.targetInfoName.setText(newValue.getName());
            } else {
                this.targetHost.clear();
                this.targetInfoName.clear();
            }
            if (this.targetClient != null) {
                this.targetClient.close();
                this.targetClient = null;
            }
        });
        this.sourceCharset.selectedItemChanged((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.sourceCharsetName.setText(newValue);
            } else {
                this.sourceCharsetName.clear();
            }
        });
        this.targetCharset.selectedItemChanged((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.targetCharsetName.setText(newValue);
            } else {
                this.targetCharsetName.clear();
            }
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 来源连接不为null，则禁用来源选项
        ZKConnect sourceInfo = this.stage.getProp("sourceInfo");
        if (sourceInfo != null) {
            this.sourceInfo.select(sourceInfo);
            this.sourceInfo.disable();
        }
        this.stage.hideOnEscape();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        this.stopTransport();
    }

    /**
     * 更新状态
     *
     * @param extraMsg 额外信息
     */
    private void updateStatus(String extraMsg) {
        if (extraMsg != null) {
            this.counter.setExtraMsg(extraMsg);
        }
        FXUtil.runLater(() -> this.transportStatus.setText(this.counter.unknownFormat()));
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.transportTitle();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        super.onStageInitialize(stage);
        this.step1.managedBindVisible();
        this.step2.managedBindVisible();
        this.step3.managedBindVisible();
    }

    @FXML
    private void showStep1() {
        this.step2.disappear();
        this.step1.display();
    }

    @FXML
    private void showStep2() {
        try {
            ZKConnect sourceInfo = this.sourceInfo.getSelectedItem();
            ZKConnect targetInfo = this.targetInfo.getSelectedItem();
            if (sourceInfo == null) {
                this.sourceInfo.requestFocus();
                MessageBox.warn(I18nHelper.pleaseSelectSourceConnect());
                return;
            }
            if (targetInfo == null) {
                this.targetInfo.requestFocus();
                MessageBox.warn(I18nHelper.pleaseSelectTargetConnect());
                return;
            }

            if (sourceInfo.compare(targetInfo)) {
                this.sourceInfo.requestFocus();
                MessageBox.warn(I18nHelper.connectionsCannotBeTheSame());
                return;
            }

            this.getStage().appendTitle("===" + I18nHelper.connectIng() + "===");
            this.getStage().disable();

            if (this.sourceClient == null || this.sourceClient.isClosed()) {
                DownLatch latch = DownLatch.of();
                ThreadUtil.start(() -> {
                    try {
                        this.sourceClient = ZKClientUtil.newClient(sourceInfo);
                        this.sourceClient.start(2500);
                    } finally {
                        latch.countDown();
                    }
                });
                if (!latch.await(3000) || !this.sourceClient.isConnected()) {
                    this.sourceClient.close();
                    this.sourceClient = null;
                    this.sourceInfo.requestFocus();
                    MessageBox.warn(I18nHelper.connectInitFail());
                    return;
                }
            }

            if (this.targetClient == null || this.targetClient.isClosed()) {
                DownLatch latch = DownLatch.of();
                ThreadUtil.start(() -> {
                    try {
                        this.targetClient = ZKClientUtil.newClient(targetInfo);
                        this.targetClient.start(2500);
                    } finally {
                        latch.countDown();
                    }
                });
                if (!latch.await(3000) || !this.targetClient.isConnected()) {
                    this.targetClient.close();
                    this.targetClient = null;
                    this.targetInfo.requestFocus();
                    MessageBox.warn(I18nHelper.connectInitFail());
                    return;
                }
            }

            this.step1.disappear();
            this.step3.disappear();
            this.step2.display();
        } finally {
            this.getStage().restoreTitle();
            this.getStage().enable();
        }
    }

    @FXML
    private void showStep3() {
        this.step2.disappear();
        this.step3.display();
    }
}
