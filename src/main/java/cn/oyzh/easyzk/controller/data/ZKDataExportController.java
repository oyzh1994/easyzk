package cn.oyzh.easyzk.controller.data;

import cn.oyzh.common.thread.DownLatch;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.handler.ZKDataExportHandler;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKClientUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FXButton;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.label.FXLabel;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.controls.textarea.MsgTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleGroup;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * zk节点导出业务
 *
 * @author oyzh
 * @since 2024/11/26
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = ZKConst.FXML_BASE_PATH + "data/zkDataExport.fxml"
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

    /**
     * 文件格式
     */
    @FXML
    private FXToggleGroup fileFormat;


    private File file;

    private String fileType;


    @FXML
    private FXText exportFile;

    @FXML
    private FXButton selectFile;

    /**
     * 适用过滤配置
     */
    @FXML
    private FXCheckBox applyFilter;

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
     * 当前zk对象
     */
    private ZKClient client;

    /**
     * 导出操作任务
     */
    private Thread execTask;

    /**
     * 过滤内容列表
     */
    private List<ZKFilter> filters;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 过滤配置储存
     */
    private final ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    /**
     * 导出处理器
     */
    private ZKDataExportHandler dataExportHandler;

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
        if (this.dataExportHandler == null) {
            this.dataExportHandler = new ZKDataExportHandler();
            this.dataExportHandler
                    .messageHandler(str -> this.exportMsg.appendLine(str))
                    .processedHandler(count -> {
                        if (count == 0) {
                            this.counter.incrIgnore(count);
                        } else if (count < 0) {
                            this.counter.incrFail(count);
                        } else {
                            this.counter.incrSuccess(count);
                        }
                        this.updateStatus(I18nHelper.migrationInProgress());
                    });
        } else {
            this.dataExportHandler.interrupt(false);
        }
        // 节点路径
        this.dataExportHandler.nodePath(this.exportPath);
        // 节点路径
        this.dataExportHandler.fileType(this.fileFormat.selectedUserData());
        // 适用过滤
        if (this.applyFilter.isSelected()) {
            this.dataExportHandler.filters(this.filterStore.loadEnable());
        } else {
            this.dataExportHandler.filters(null);
        }
        this.dataExportHandler.client(this.client);
        this.dataExportHandler.exportFile(this.file);
        // 执行导出
        this.execTask = ThreadUtil.start(() -> {
            try {
                this.dataExportHandler.doExport();
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
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageInitialize(StageAdapter stage) {
        super.onStageInitialize(stage);
        this.step1.managedBindVisible();
        this.step2.managedBindVisible();
        this.step3.managedBindVisible();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        this.stopExport();
    }

    /**
     * 获取此节点所有节点
     *
     * @param path       节点路径
     * @param properties 要获取的属性
     * @param allNodes   所有节点列表
     */
    private void getNodeAll(@NonNull String path, @NonNull String properties, @NonNull List<ZKNode> allNodes) {
        try {
            // 取消操作
            if (ThreadUtil.isInterrupted(this.execTask)) {
                return;
            }
            // 获取节点
            ZKNode zkNode = ZKNodeUtil.getNode(this.client, path, properties);
            // 失败
            if (zkNode == null) {
                this.updateStatus(path, 0, null);
            } else if (this.applyFilter.isSelected() && ZKNodeUtil.isFiltered(zkNode.nodePath(), this.filters)) { // 被过滤
                this.updateStatus(path, 2, null);
            } else {// 添加到集合
                allNodes.add(zkNode);
                this.updateStatus(path, 1, null);
            }
            // 处理子节点
            if (zkNode != null && zkNode.isParent()) {
                // 获取子节点路径
                List<String> subs = this.client.getChildren(path);
                // 递归获取节点
                for (String sub : subs) {
                    if (!ThreadUtil.isInterrupted(this.execTask)) {
                        this.getNodeAll(ZKNodeUtil.concatPath(path, sub), properties, allNodes);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            this.updateStatus(path, 0, ex);
        }
    }

    /**
     * 更新状态
     *
     * @param path   路径
     * @param status 状态 0:失败 1:成功 2:过滤
     * @param ex     异常信息
     */
    private void updateStatus(String path, int status, Exception ex) {
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        this.counter.update(status);
        String msg;
        if (status == 1) {
            msg = I18nHelper.exportNode() + " " + path + " " + I18nHelper.success();
        } else if (status == 2) {
            msg = I18nHelper.exportNode() + " " + path + " " + ZKI18nHelper.nodeTip4();
        } else {
            msg = I18nHelper.exportNode() + " " + path + " " + I18nHelper.fail();
            if (ex != null) {
                msg += "，" + I18nHelper.errorInfo() + ZKExceptionParser.INSTANCE.apply(ex);
            }
        }
        this.exportMsg.appendLine(msg);
        this.updateStatus(null);
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
        return I18nResourceBundle.i18nString("base.title.export");
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
        this.fileType = this.fileFormat.selectedUserData();
        this.step2.display();
    }

    @FXML
    private void showStep3() {
        if (this.file == null) {
            this.selectFile.requestFocus();
            MessageBox.warn(I18nHelper.pleaseSelectFile());
            return;
        }
        if (this.client == null || this.client.isClosed()) {
            this.getStage().appendTitle("===" + I18nHelper.connectIng() + "===");
            this.getStage().disable();
            DownLatch latch = DownLatch.of();
            ThreadUtil.start(() -> {
                try {
                    this.client = ZKClientUtil.newClient(this.connect);
                    this.client.start();
                } finally {
                    latch.countDown();
                }
            }, 100);
            if (!latch.await(3000) || !this.client.isConnected()) {
                this.client.close();
                this.client = null;
                MessageBox.warn(I18nHelper.connectInitFail());
                return;
            }
        }
        try {
            this.step1.disappear();
            this.step2.disappear();
            this.step3.display();
        } finally {
            this.getStage().restoreTitle();
            this.getStage().enable();
        }
    }

    @FXML
    private void selectFile() {
        FileExtensionFilter filter = null;
        if ("json".equals(this.fileType)) {
            filter = FileChooserHelper.jsonExtensionFilter();
        } else if ("xls".equals(this.fileType)) {
            filter = FileChooserHelper.xlsExtensionFilter();
        } else if ("xlsx".equals(this.fileType)) {
            filter = FileChooserHelper.xlsxExtensionFilter();
        }
        String fileName = "ZK-" + this.connect.getName() + "-" + I18nHelper.exportData() + "." + this.fileType;
        this.file = FileChooserHelper.save(fileName, fileName, filter);
        if (this.file != null) {
            this.exportFile.setText(this.file.getPath());
        } else {
            this.exportFile.clear();
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.connect = this.getWindowProp("connect");
        this.exportPath = this.getWindowProp("nodePath");
    }
}
