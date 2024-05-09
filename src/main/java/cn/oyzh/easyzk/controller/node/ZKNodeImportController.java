package cn.oyzh.easyzk.controller.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.dto.ZKNodeExport;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.util.ZKExportUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.CharsetComboBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.handler.StateManager;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.util.FileChooserUtil;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * zk节点导入业务
 *
 * @author oyzh
 * @since 2020/10/14
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "node/zkNodeImport.fxml"
)
public class ZKNodeImportController extends Controller {

    /**
     * zk客户端
     */
    private ZKClient zkClient;

    /**
     * 脚本信息
     */
    @FXML
    private FlexText scriptInfo;

    /**
     * 忽略已存在的节点
     */
    @FXML
    private FlexCheckBox ignoreExist;

    /**
     * 导入字符集
     */
    @FXML
    private CharsetComboBox charset;

    /**
     * 导入按钮
     */
    @FXML
    private FlexButton importBtn;

    /**
     * 状态管理器
     */
    @FXML
    private StateManager stateManager;

    /**
     * 结束导入按钮
     */
    @FXML
    private FlexButton stopImportBtn;

    /**
     * 导入状态
     */
    @FXML
    private FXLabel importStatus;

    /**
     * 导入消息
     */
    @FXML
    private MsgTextArea importMsg;

    /**
     * 导入操作任务
     */
    private Thread importTask;

    /**
     * 导入数据
     */
    private ZKNodeExport nodeExport;

    /**
     * 计数器
     */
    private final Counter counter = new Counter();

    /**
     * 拖拽文件
     *
     * @param event 事件
     */
    private void dragFile(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        List<File> files = dragboard.getFiles();
        if (CollUtil.isEmpty(files)) {
            return;
        }
        if (files.size() != 1) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.onlySupport", "base.single", "base.file"));
            return;
        }
        File file = files.getFirst();
        // 解析文件
        this.parseFile(file);
    }

    /**
     * 选择脚本文件
     */
    @FXML
    private void chooseFile() {
        FileChooser.ExtensionFilter filter1 = new FileChooser.ExtensionFilter("JSON files|TXT files", "*.json", "*.txt");
        FileChooser.ExtensionFilter filter2 = new FileChooser.ExtensionFilter("All", "*.*");
        File file = FileChooserUtil.choose(I18nResourceBundle.i18nString("base.choose", "base.file"), new FileChooser.ExtensionFilter[]{filter1, filter2});
        // 解析文件
        this.parseFile(file);
    }

    /**
     * 解析文件
     *
     * @param file 文件
     */
    private void parseFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.file", "base.notExists"));
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.notSupport", "base.folder"));
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "txt", "text", "json")) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.invalid", "base.format"));
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn(I18nResourceBundle.i18nString("base.contentNotEmpty"));
            return;
        }
        try {
            // 解析数据
            this.nodeExport = ZKExportUtil.fromFile(file);
            // 初始化信息
            this.importMsg.clear();
            this.importBtn.enable();
            // 脚本信息
            String info = I18nResourceBundle.i18nString("base.fileName") + " " + file.getName() + "，" +
                    I18nResourceBundle.i18nString("base.total") + " " + this.nodeExport.counts() + I18nResourceBundle.i18nString("base.line") + "，" +
                    I18nResourceBundle.i18nString("base.size") + " " + Math.max(1, file.length() / 1024) + "Kb，" +
                    I18nResourceBundle.i18nString("base.version") + " " + this.nodeExport.version() + "，" +
                    I18nResourceBundle.i18nString("base.platform") + " " + this.nodeExport.platform() + "，" +
                    I18nResourceBundle.i18nString("base.charset") + " " + this.nodeExport.charset();
            if (this.nodeExport.hasPrefix()) {
                info += "，" + I18nResourceBundle.i18nString("base.prefix") + this.nodeExport.getPrefix();
            }
            this.scriptInfo.setText(info);
            this.charset.select(this.nodeExport.getCharset());
        } catch (Exception ex) {
            ex.printStackTrace();
            this.nodeExport = null;
            this.importBtn.disable();
            MessageBox.exception(ex, I18nResourceBundle.i18nString("base.parseFail"));
        }
    }

    /**
     * 执行导入
     */
    @FXML
    private void doImport() {
        // 重置参数
        this.importMsg.clear();
        this.counter.reset();
        this.counter.setSum(this.nodeExport.counts());
        // 开始处理
        this.importBtn.disable();
        this.stateManager.disable();
        this.stage.appendTitle("===" + I18nResourceBundle.i18nString("base.import", "base.processing") + "===");
        // 忽略已存在节点
        boolean ignoreExist = this.ignoreExist.isSelected();
        // 执行导入
        this.importTask = ThreadUtil.start(() -> {
            try {
                this.stopImportBtn.enable();
                for (Map<String, String> zkNode : this.nodeExport.getNodes()) {
                    // 取消操作
                    if (ThreadUtil.isInterrupted(this.importTask)) {
                        StaticLog.warn("import cancel!");
                        break;
                    }
                    // 获取数据
                    String path = zkNode.get("path");
                    String data = zkNode.get("data");
                    byte[] bytes = this.nodeExport.getDateBytes(data, this.charset.getCharset());
                    // 状态
                    int status = 1;
                    // 异常
                    Exception exception = null;
                    try {
                        // 设置数据
                        if (this.zkClient.exists(path)) {
                            if (ignoreExist) {
                                status = 2;
                            } else {
                                this.zkClient.setData(path, bytes);
                            }
                        } else {// 创建节点
                            this.zkClient.createIncludeParents(path, bytes);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        status = 0;
                        exception = ex;
                    }
                    this.updateStatus(path, status, exception);
                }
                // 收尾工作
                this.updateStatus(I18nResourceBundle.i18nString("base.processing"));
                this.updateStatus(I18nResourceBundle.i18nString("base.actionSuccess"));
                MessageBox.okToast(I18nResourceBundle.i18nString("base.actionSuccess"));

            } catch (Exception ex) {
                if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus(I18nResourceBundle.i18nString("base.actionCancel"));
                    MessageBox.okToast(I18nResourceBundle.i18nString("base.actionCancel"));
                } else {
                    ex.printStackTrace();
                    this.updateStatus(I18nResourceBundle.i18nString("base.actionFail"));
                    MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
                }
            } finally {
                // 结束处理
                this.importBtn.enable();
                this.stateManager.enable();
                this.stopImportBtn.disable();
                this.stage.restoreTitle();
            }
        });
    }

    /**
     * 结束导入
     */
    @FXML
    private void stopImport() {
        ThreadUtil.interrupt(this.importTask);
        this.importTask = null;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.zkClient = this.getStageProp("zkClient");
        this.scriptInfo.managedProperty().bind(this.scriptInfo.visibleProperty());
        this.scriptInfo.addTextChangeListener((observableValue, s, t1) -> this.scriptInfo.setVisible(StrUtil.isNotBlank(t1)));
        this.stage.hideOnEscape();
        // 文件拖拽相关
        this.stage.scene().setOnDragOver(event1 -> {
            this.stage.disable();
            this.stage.appendTitle("===" + I18nResourceBundle.i18nString("base.dragTip1") + "===");
            event1.acceptTransferModes(TransferMode.ANY);
            event1.consume();
        });
        this.stage.scene().setOnDragExited(event1 -> {
            this.stage.enable();
            this.stage.restoreTitle();
            event1.consume();
        });
        this.stage.scene().setOnDragDropped(event1 -> {
            this.dragFile(event1);
            event1.setDropCompleted(true);
            event1.consume();
        });
    }

    @Override
    public void onStageHidden(WindowEvent event) {
        this.stopImport();
    }

    /**
     * 更新状态
     *
     * @param path   路径
     * @param status 状态 0:失败 1:成功 2:忽略
     * @param ex     异常
     */
    private void updateStatus(String path, int status, Exception ex) {
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        this.counter.update(status);
        String msg;
        if (status == 1) {
            msg = I18nResourceBundle.i18nString("base.importNode") + " " + path + " " + I18nResourceBundle.i18nString("base.success");
        } else if (status == 2) {
            msg = I18nResourceBundle.i18nString("base.importNode") + " " + path + " " + I18nResourceBundle.i18nString("base.nodeTip1");
        } else {
            msg = I18nResourceBundle.i18nString("base.importNode") + " " + path + " " + I18nResourceBundle.i18nString("base.fail");
            if (ex != null) {
                msg += "，" + I18nResourceBundle.i18nString("base.errorInfo") + ZKExceptionParser.INSTANCE.apply(ex);
            }
        }
        this.importMsg.appendLine(msg);
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
        FXUtil.runLater(() -> this.importStatus.setText(this.counter.knownFormat()));
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.import");
    }
}
