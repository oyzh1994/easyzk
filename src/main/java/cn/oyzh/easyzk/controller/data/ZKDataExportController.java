package cn.oyzh.easyzk.controller.data;

import cn.oyzh.common.file.FileNameUtil;
import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.thread.DownLatch;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.handler.ZKDataExportHandler;
import cn.oyzh.easyzk.store.ZKFilterStore;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKClientUtil;
import cn.oyzh.fx.gui.combobox.CharsetComboBox;
import cn.oyzh.fx.gui.text.area.MsgTextArea;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FXButton;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.controls.toggle.FXToggleGroup;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.tray.TrayManager;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;


/**
 * zk数据导出业务
 *
 * @author oyzh
 * @since 2024/11/26
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "data/zkDataExport.fxml"
)
public class ZKDataExportController extends StageController {

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

    // /**
    //  * 第四步
    //  */
    // @FXML
    // private FlexVBox step4;

    /**
     * 导出文件
     */
    private File exportFile;

    /**
     * 文件格式
     */
    @FXML
    private FXToggleGroup format;

    /**
     * 前缀
     */
    @FXML
    private FXToggleGroup prefix;

    /**
     * 文件名
     */
    @FXML
    private FXText fileName;

    /**
     * 节点路径
     */
    @FXML
    private FXText nodePath;

    /**
     * 字符集
     */
    @FXML
    private CharsetComboBox charset;

    /**
     * 选择文件
     */
    @FXML
    private FXButton selectFile;

    /**
     * 适用过滤配置
     */
    @FXML
    private FXCheckBox applyFilter;

    /**
     * 包含标题
     */
    @FXML
    private FXCheckBox includeTitle;

    /**
     * 压缩
     */
    @FXML
    private FXCheckBox compress;

    /**
     * 结束导出按钮
     */
    @FXML
    private FlexButton stopExportBtn;

    /**
     * 导出状态
     */
    @FXML
    private FXLabel exportStatus;

    /**
     * 导出消息
     */
    @FXML
    private MsgTextArea exportMsg;

    /*
     * 导出路径
     */
    private String exportPath;

    /**
     * 当前zk对象
     */
    private ZKConnect connect;

    /**
     * 当前zk客户端
     */
    private ZKClient client;

    /**
     * 导出操作任务
     */
    private Thread execTask;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 过滤配置储存
     */
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * 导出处理器
     */
    private ZKDataExportHandler exportHandler;

    /**
     * 执行导出
     */
    @FXML
    private void doExport() {
        // 重置参数
        // 清理信息
        this.counter.reset();
        this.exportMsg.clear();
        this.exportStatus.clear();
        NodeGroupUtil.disable(this.stage, "exec");
        this.stage.appendTitle("===" + I18nHelper.exportProcessing() + "===");
        // 生成迁移处理器
        if (this.exportHandler == null) {
            this.exportHandler = new ZKDataExportHandler();
            this.exportHandler.messageHandler(str -> this.exportMsg.appendLine(str))
                    .processedHandler(count -> {
                        if (count == 0) {
                            this.counter.updateIgnore();
                        } else if (count < 0) {
                            this.counter.incrFail(count);
                        } else {
                            this.counter.incrSuccess(count);
                        }
                        this.updateStatus(I18nHelper.exportInProgress());
                    });
        } else {
            this.exportHandler.interrupt(false);
        }
        String fileType = this.format.selectedUserData();
        // 文件类型
        this.exportHandler.fileType(fileType);
        // 客户端
        this.exportHandler.client(this.client);
        // 节点路径
        this.exportHandler.nodePath(this.exportPath);
        // 导出文件
        this.exportHandler.filePath(this.exportFile.getPath());
        // 字符集
        this.exportHandler.charset(this.charset.getCharsetName());
        // 适用过滤
        if (this.applyFilter.isSelected()) {
            this.exportHandler.filters(this.filterStore.loadEnable(this.client.iid()));
        } else {
            this.exportHandler.filters(null);
        }
        // 包含标题
        this.exportHandler.includeTitle(this.includeTitle.isEnable() && this.includeTitle.isSelected());
        // 压缩
        this.exportHandler.compress(this.compress.isEnable() && this.compress.isSelected());
        // 前缀
        if (FileNameUtil.isTxtType(fileType)) {
            this.exportHandler.prefix(this.prefix.selectedUserData());
        }
        // 执行导出
        this.execTask = ThreadUtil.start(() -> {
            try {
                this.stopExportBtn.enable();
                // 更新状态
                this.updateStatus(I18nHelper.exportStarting());
                // 执行导出
                this.exportHandler.doExport();
                // 更新状态
                this.updateStatus(I18nHelper.exportFinished());
                // 发送消息到托盘
                if (!this.stage.isFocused()) {
                    TrayManager.displayInfoMessage(I18nHelper.tips(), I18nHelper.exportFinished());
                }
            } catch (Exception e) {
                if (e.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus(I18nHelper.operationCancel());
                    MessageBox.warn(I18nHelper.operationCancel());
                } else {
                    e.printStackTrace();
                    this.updateStatus(I18nHelper.operationFail());
                    MessageBox.warn(I18nHelper.operationFail());
                }
            } finally {
                NodeGroupUtil.enable(this.stage, "exec");
                this.stopExportBtn.disable();
                this.stage.restoreTitle();
            }
        });
    }

    /**
     * 结束导出
     */
    @FXML
    private void stopExport() {
        ThreadUtil.interrupt(this.execTask);
        this.execTask = null;
        if (this.exportHandler != null) {
            this.exportHandler.interrupt();
        }
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.stage.hideOnEscape();
        // 格式选择监听
        this.format.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            this.exportFile = null;
            this.fileName.clear();
        });
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        this.stopExport();
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
        FXUtil.runLater(() -> this.exportStatus.setText(this.counter.unknownFormat()));
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.exportTitle();
    }

    @FXML
    private void showStep1() {
        this.step2.disappear();
        this.step3.disappear();
        // this.step4.disappear();
        this.step1.display();
    }

    @FXML
    private void showStep2() {
        this.step1.disappear();
        this.step3.disappear();
        // this.step4.disappear();
        String fileType = this.format.selectedUserData();
        // 检查是否支持前缀
        if (FileNameUtil.isTxtType(fileType)) {
            NodeGroupUtil.enable(this.stage, "txt");
        } else {
            NodeGroupUtil.disable(this.stage, "txt");
        }
        // 检查是否支持标题
        if (StringUtil.equalsAny(fileType, "xls", "xlsx", "csv")) {
            this.includeTitle.enable();
        } else {
            this.includeTitle.disable();
        }
        // 检查是否支持压缩
        if (StringUtil.equalsAny(fileType, "xml", "json")) {
            this.compress.enable();
        } else {
            this.compress.disable();
        }
        this.step2.display();
    }

    // @FXML
    // private void showStep3() {
    //     if (this.exportFile == null) {
    //         this.selectFile.requestFocus();
    //         MessageBox.warn(I18nHelper.pleaseSelectFile());
    //         return;
    //     }
    //     this.step1.disappear();
    //     this.step2.disappear();
    //     this.step4.disappear();
    //     this.step3.display();
    // }

    @FXML
    private void showStep3() {
        try {
            // 检查文件
            if (this.exportFile == null) {
                this.selectFile.requestFocus();
                MessageBox.warn(I18nHelper.pleaseSelectFile());
                return;
            }
            // 检查客户端
            if (this.client == null || this.client.isClosed()) {
                this.getStage().appendTitle("===" + I18nHelper.connectIng() + "===");
                this.getStage().disable();
                DownLatch latch = DownLatch.of();
                ThreadUtil.start(() -> {
                    try {
                        this.client = ZKClientUtil.newClient(this.connect);
                        this.client.start(2500);
                    } finally {
                        latch.countDown();
                    }
                });
                if (!latch.await(3000) || !this.client.isConnected()) {
                    this.client.close();
                    this.client = null;
                    MessageBox.warn(I18nHelper.connectInitFail());
                    return;
                }
            }

            this.step1.disappear();
            this.step2.disappear();
            // this.step3.disappear();
            this.step3.display();
        } finally {
            this.getStage().restoreTitle();
            this.getStage().enable();
        }
    }

    /**
     * 选择文件
     */
    @FXML
    private void selectFile() {
        String fileType = this.format.selectedUserData();
        FileExtensionFilter filter = FileChooserHelper.extensionFilter(fileType);
        String fileName = "Zookeeper-" + this.connect.getName() + "-" + I18nHelper.exportData() + "." + fileType;
        this.exportFile = FileChooserHelper.save(fileName, fileName, filter);
        if (this.exportFile != null) {
            // 删除文件
            if (this.exportFile.exists()) {
                FileUtil.del(this.exportFile);
            }
            this.fileName.setText(this.exportFile.getPath());
        } else {
            this.fileName.clear();
        }
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.connect = this.getWindowProp("connect");
        this.exportPath = this.getWindowProp("nodePath");
        this.nodePath.setText(this.exportPath);
    }
}
