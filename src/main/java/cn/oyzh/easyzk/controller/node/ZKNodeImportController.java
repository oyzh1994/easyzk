package cn.oyzh.easyzk.controller.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.dto.ZKNodeExport;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.parser.ZKExceptionParser;
import cn.oyzh.easyzk.util.ZKExportUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.CharsetComboBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.text.FlexText;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.handler.StateManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXFileChooser;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.fxml.FXML;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@StageAttribute(
        title = "zk数据导入",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = ZKStyle.COMMON,
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
    private Counter counter;

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
            MessageBox.warn("仅支持单个文件！");
            return;
        }
        File file = files.get(0);
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
        File file = FXFileChooser.choose("选择zk脚本", new FileChooser.ExtensionFilter[]{filter1, filter2});
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
            MessageBox.warn("文件不存在！");
            return;
        }
        if (file.isDirectory()) {
            MessageBox.warn("不支持文件夹！");
            return;
        }
        if (!FileNameUtil.isType(file.getName(), "txt", "json")) {
            MessageBox.warn("仅支持txt或json文件！");
            return;
        }
        if (file.length() == 0) {
            MessageBox.warn("文件内容为空！");
            return;
        }
        try {
            // 解析数据
            this.nodeExport = ZKExportUtil.fromFile(file);
            // 初始化信息
            this.importMsg.clear();
            this.importBtn.enable();
            // 脚本信息
            String info = "文件名：" + file.getName() + "，" +
                    "共：" + this.nodeExport.counts() + "行，" +
                    "大小：" + Math.max(1, file.length() / 1024) + "Kb，" +
                    "源版本：" + this.nodeExport.version() + "，" +
                    "源平台：" + this.nodeExport.platform() + "，" +
                    "字符集：" + this.nodeExport.charset();
            if (this.nodeExport.hasPrefix()) {
                info += "，脚本前缀：" + this.nodeExport.getPrefix();
            }
            this.scriptInfo.setText(info);
            this.charset.select(this.nodeExport.getCharset());
        } catch (Exception ex) {
            ex.printStackTrace();
            this.nodeExport = null;
            this.importBtn.disable();
            MessageBox.exception(ex, "解析脚本失败");
        }
    }

    /**
     * 执行导入
     */
    @FXML
    private void doImport() {
        // 重置参数
        this.importMsg.clear();
        if (this.counter == null) {
            this.counter = new Counter();
        } else {
            this.counter.reset();
        }
        this.counter.setSum(this.nodeExport.counts());
        // 开始处理
        this.stateManager.disable();
        this.stage.appendTitle("===导入执行中===");
        // 忽略已存在节点
        boolean ignoreExist = this.ignoreExist.isSelected();
        // 执行导入
        this.importTask = ThreadUtil.start(() -> {
            try {
                this.stopImportBtn.enable();
                EventUtil.fire(ZKEventTypes.ZK_IMPORT_START);
                for (Map<String, String> zkNode : this.nodeExport.getNodes()) {
                    // 取消操作
                    if (ThreadUtil.isInterrupted(this.importTask)) {
                        log.warn("import cancel!");
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
                this.updateStatus("数据导入收尾中...");
                this.importMsg.waitTextExpend();
                this.updateStatus("数据导入结束");
                MessageBox.okToast("导入数据结束！");
            } catch (Exception ex) {
                if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus("数据导入取消");
                    MessageBox.okToast("导入数据取消！");
                } else {
                    ex.printStackTrace();
                    this.updateStatus("数据导入失败");
                    MessageBox.warn("导入数据失败！");
                }
            } finally {
                // 结束处理
                this.stateManager.enable();
                this.stopImportBtn.disable();
                this.stage.restoreTitle();
                EventUtil.fire(ZKEventTypes.ZK_IMPORT_FINISH);
                SystemUtil.gcLater();
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
            this.stage.appendTitle("===松开鼠标以释放文件===");
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
            msg = "导入节点：" + path + " 成功";
        } else if (status == 2) {
            msg = "导入节点：" + path + " 已忽略，此节点已存在";
        } else {
            msg = "导入节点：" + path + " 失败";
            if (ex != null) {
                msg += "，错误信息：" + ZKExceptionParser.INSTANCE.apply(ex);
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
}
