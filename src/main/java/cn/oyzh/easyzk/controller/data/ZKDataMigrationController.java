package cn.oyzh.easyzk.controller.data;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.system.SystemUtil;
import cn.oyzh.easyzk.handler.ZKDataMigrationHandler;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.easyzk.util.ZKProcessUtil;
import cn.oyzh.fx.gui.text.area.MsgTextArea;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.toggle.FXToggleGroup;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * zk迁移业务
 *
 * @author oyzh
 * @since 2024/11/25
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "data/zkDataMigration.fxml"
)
public class ZKDataMigrationController extends StageController {

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
     * 迁移消息
     */
    @FXML
    private MsgTextArea migrationMsg;

    /**
     * 迁移状态
     */
    @FXML
    private FXLabel migrationStatus;

    /**
     * 数据处理策略
     */
    @FXML
    private FXToggleGroup dataPolicy;

    /**
     * 分组
     */
    @FXML
    private FXCheckBox groups;

    /**
     * 过滤
     */
    @FXML
    private FXCheckBox filters;

    /**
     * 认证信息
     */
    @FXML
    private FXCheckBox authInfos;

    /**
     * 连接
     */
    @FXML
    private FXCheckBox connections;

    /**
     * 终端历史
     */
    @FXML
    private FXCheckBox terminalHistory;

    /**
     * 应用配置
     */
    @FXML
    private FXCheckBox applicationSetting;

    /**
     * 迁移操作任务
     */
    private Thread execTask;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 迁移处理器
     */
    private ZKDataMigrationHandler migrationHandler;

    /**
     * 执行迁移
     */
    @FXML
    private void doMigration() {
        // 重置参数
        this.counter.reset();
        // 清理信息
        this.migrationMsg.clear();
        this.migrationStatus.clear();
        // 开始处理
        NodeGroupUtil.disable(this.stage, "exec");
        this.stage.appendTitle("===" + I18nHelper.migrationInProgress() + "===");
        // 生成迁移处理器
        if (this.migrationHandler == null) {
            this.migrationHandler = new ZKDataMigrationHandler();
            this.migrationHandler
                    .messageHandler(str -> this.migrationMsg.appendLine(str))
                    .processedHandler(count -> {
                        if (count == 0) {
                            this.counter.updateIgnore();
                        } else if (count < 0) {
                            this.counter.incrFail(count);
                        } else {
                            this.counter.incrSuccess(count);
                        }
                        this.updateStatus(I18nHelper.migrationInProgress());
                    });
        } else {
            this.migrationHandler.interrupt(false);
        }
        // 分组
        this.migrationHandler.groups(this.groups.isSelected());
        // 过滤
        this.migrationHandler.filters(this.filters.isSelected());
        // 认证信息
        this.migrationHandler.authInfos(this.authInfos.isSelected());
        // 连接
        this.migrationHandler.connections(this.connections.isSelected());
        // 终端历史
        this.migrationHandler.terminalHistory(this.terminalHistory.isSelected());
        // 应用配置
        this.migrationHandler.applicationSetting(this.applicationSetting.isSelected());
        // 数据处理策略
        this.migrationHandler.dataPolicy(this.dataPolicy.selectedUserData());
        // 开始处理
        NodeGroupUtil.disable(this.stage, "exec");
        this.stage.appendTitle("===" + I18nHelper.migrationInProgress() + "===");
        // 执行迁移
        this.execTask = ThreadUtil.start(() -> {
            try {
                this.updateStatus(I18nHelper.migrationStarting());
                // 执行迁移
                this.migrationHandler.doMigration();
                // 标记为完成迁移
                ZKStoreUtil.doneMigration();
                // 更新状态
                this.updateStatus(I18nHelper.migrationFinished());
                // 重启应用
                if (MessageBox.confirm(ZKI18nHelper.migrationTip8())) {
                    this.closeWindow();
                    ZKProcessUtil.restartApplication();
                }
            } catch (Exception ex) {
                if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus(I18nHelper.operationCancel());
                } else {
                    if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                        this.updateStatus(I18nHelper.operationCancel());
                    } else {
                        ex.printStackTrace();
                        this.updateStatus(I18nHelper.operationFail());
                    }
                }
            } finally {
                // 结束处理
                NodeGroupUtil.enable(this.stage, "exec");
                this.stage.restoreTitle();
                SystemUtil.gcLater();
            }
        });
    }

    /**
     * 结束迁移
     */
    @FXML
    private void stopMigration() {
        ThreadUtil.interrupt(this.execTask);
        this.execTask = null;
        if (this.migrationHandler != null) {
            this.migrationHandler.interrupt();
        }
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
        FXUtil.runLater(() -> this.migrationStatus.setText(this.counter.unknownFormat()));
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.stage.hideOnEscape();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        this.stopMigration();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.migrationTitle();
    }

    @FXML
    private void showStep1() {
        this.step2.disappear();
        this.step3.disappear();
        this.step1.display();
    }

    @FXML
    private void showStep2() {
        this.step1.disappear();
        this.step3.disappear();
        this.step2.display();
    }

    @FXML
    private void showStep3() {
        try {
            this.step1.disappear();
            this.step2.disappear();
            this.step3.display();
        } finally {
            this.getStage().restoreTitle();
            this.getStage().enable();
        }
    }
}
