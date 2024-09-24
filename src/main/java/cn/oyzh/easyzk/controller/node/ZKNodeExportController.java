package cn.oyzh.easyzk.controller.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKAuthStore2;
import cn.oyzh.easyzk.store.ZKFilterStore2;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKExportUtil;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.file.FileChooserHelper;
import cn.oyzh.fx.plus.file.FileExtensionFilter;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.Counter;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * zk节点导出业务
 *
 * @author oyzh
 * @since 2020/10/13
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "node/zkNodeExport.fxml"
)
public class ZKNodeExportController extends StageController {

    /**
     * 节点路径组件
     */
    @FXML
    private FlexHBox nodePathBox;

    /**
     * 节点路径
     */
    @FXML
    private TextField nodePath;

    /**
     * 按词典导出
     */
    @FXML
    private FlexCheckBox dictSort;

    /**
     * 适用过滤配置
     */
    @FXML
    private FlexCheckBox applyFilter;

    // /**
    //  * 格式选项
    //  */
    // @FXML
    // private FlexComboBox<String> format;
    //
    // /**
    //  * 前缀选项
    //  */
    // @FXML
    // private FlexComboBox<String> prefix;
    //
    // /**
    //  * 美化选项
    //  */
    // @FXML
    // private FlexComboBox<String> pretty;
    //
    // /**
    //  * 美化组件
    //  */
    // @FXML
    // private FlexFlowPane prettyPane;

    /**
     * 是否开启美化
     */
    @FXML
    private FXToggleSwitch pretty;

    // /**
    //  * 前缀组件
    //  */
    // @FXML
    // private FlexFlowPane prefixPane;

    /**
     * 结束导出按钮
     */
    @FXML
    private FlexButton stopExportBtn;

    // /**
    //  * 状态管理器
    //  */
    // @FXML
    // private StateManager stateManager;

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
    private final ZKFilterStore2 filterStore = ZKFilterStore2.INSTANCE;

    /**
     * 执行导出
     */
    @FXML
    private void doExport() {
        // 重置参数
        this.counter.reset();
        this.exportMsg.clear();
        boolean dictSort = this.dictSort.isSelected();
        String properties = ZKNodeUtil.DATA_PROPERTIES + ZKNodeUtil.STAT_PROPERTIES;
        // 开始处理
        this.exportMsg.clear();
        // this.stateManager.disable();
        NodeGroupUtil.disable(this.stage, "exec");
        this.stage.appendTitle("===" + I18nHelper.exportProcessing() + "===");
        // 适用过滤
        if (this.applyFilter.isSelected()) {
            this.filters = this.filterStore.loadEnable();
        }
        // 执行导出
        this.execTask = ThreadUtil.start(() -> {
            try {
                this.stopExportBtn.enable();
                // 初始化连接
                if (!this.client.isConnected()) {
                    this.updateStatus(I18nHelper.connectInitIng() + "...");
                    this.client.start();
                    if (!this.client.isConnected()) {
                        MessageBox.okToast(I18nHelper.connectInitFail());
                        return;
                    }
                    this.updateStatus(I18nHelper.exportProcessing());
                }
                // 获取节点
                List<ZKNode> zkNodes = new ArrayList<>();
                this.getNodeAll(this.exportPath, properties, zkNodes);
                // 取消操作
                if (ThreadUtil.isInterrupted(this.execTask)) {
                    StaticLog.warn("export canceled!");
                    return;
                }
                // 排除临时节点
                zkNodes = zkNodes.parallelStream().filter(ZKNode::persistent).collect(Collectors.toList());
                if (CollUtil.isEmpty(zkNodes)) {
                    MessageBox.okToast(I18nHelper.operationFail());
                    return;
                }
                // 节点按词典顺序排序
                if (dictSort) {
                    zkNodes.sort(ZKNode::compareTo);
                }
                // // 判断是否json格式
                // boolean isJSON = this.format.getSelectedIndex() == 0;
                // 导出内容
                String exportData;
                // 文件格式
                FileExtensionFilter extensionFilter;
                // 处理名称
                String fileName = "ZK-" + I18nHelper.connect() + this.client.infoName() + "-" + I18nHelper.exportData();
                // if (isJSON) {
                // boolean prettyFormat = this.pretty.getSelectedIndex() == 0;
                boolean prettyFormat = this.pretty.isSelected();
                exportData = ZKExportUtil.nodesToJSON(zkNodes, CharsetUtil.defaultCharsetName(), prettyFormat);
                extensionFilter = FileChooserHelper.jsonExtensionFilter();
                fileName += ".json";
                // } else {
                //     // 前缀
                //     String prefixVal = StrUtil.equalsAny(this.prefix.getValue(), "set", "create") ? this.prefix.getValue() : "";
                //     exportData = ZKExportUtil.nodesToTxt(zkNodes, CharsetUtil.defaultCharsetName(), prefixVal);
                //     extensionFilter = new FileChooser.ExtensionFilter("TXT files", "*.txt");
                //     fileName += ".txt";
                // }
                // 收尾工作
                this.updateStatus(I18nHelper.fileProcessing());
                File file = FileChooserHelper.save(I18nHelper.exportData(), fileName, extensionFilter);
                // 保存文件
                if (file != null) {
                    FileUtil.writeUtf8String(exportData, file);
                    this.updateStatus(I18nHelper.operationSuccess());
                    MessageBox.okToast(I18nHelper.operationSuccess());
                } else {
                    this.updateStatus(I18nHelper.operationCancel());
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
                // 结束处理
                // this.stateManager.enable();
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
        this.stage.hideOnEscape();
        // // 格式变化处理
        // this.format.selectedIndexChanged((observableValue, number, t1) -> {
        //     if (t1.intValue() == 0) {
        //         this.prefixPane.disappear();
        //         this.prettyPane.display();
        //     } else {
        //         this.prefixPane.display();
        //         this.prettyPane.disappear();
        //     }
        // });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        TreeItem<?> item = this.getWindowProp("zkItem");
        if (item instanceof ZKNodeTreeItem treeItem) {
            this.client = this.getWindowProp("zkClient");
            this.exportPath = treeItem.nodePath();
            this.nodePath.setText(treeItem.decodeNodePath());
        } else if (item instanceof ZKConnectTreeItem treeItem) {
            this.nodePathBox.managedBindVisible();
            this.client = new ZKClient(treeItem.value());
            this.exportPath = "/";
            this.nodePathBox.disappear();
        }
        this.stage.hideOnEscape();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
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
            if (zkNode != null && zkNode.parentNode()) {
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
}
