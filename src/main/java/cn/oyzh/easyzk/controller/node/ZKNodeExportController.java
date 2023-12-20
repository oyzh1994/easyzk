package cn.oyzh.easyzk.controller.node;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKFilterStore;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKExportUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.Counter;
import cn.oyzh.fx.common.util.SystemUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexFlowPane;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.controls.button.FlexButton;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.handler.StateManager;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.util.FileChooserUtil;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
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
        title = "数据导出",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        // cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "node/zkNodeExport.fxml"
)
public class ZKNodeExportController extends Controller {

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

    /**
     * 格式选项
     */
    @FXML
    private FlexComboBox<String> format;

    /**
     * 前缀选项
     */
    @FXML
    private FlexComboBox<String> prefix;

    /**
     * 美化选项
     */
    @FXML
    private FlexComboBox<String> pretty;

    /**
     * 美化组件
     */
    @FXML
    private FlexFlowPane prettyPane;

    /**
     * 前缀组件
     */
    @FXML
    private FlexFlowPane prefixPane;

    // /**
    //  * 导出字符集
    //  */
    // @FXML
    // private CharsetComboBox charset;

    /**
     * 结束导出按钮
     */
    @FXML
    private FlexButton stopExportBtn;

    /**
     * 状态管理器
     */
    @FXML
    private StateManager stateManager;

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
    private ZKClient zkClient;

    /**
     * 导出操作任务
     */
    private Thread exportTask;

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
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * 执行导出
     */
    @FXML
    private void doExport() {
        // 重置参数
        this.counter.reset();
        this.exportMsg.clear();
        // 执行参数
//        String path = this.zkItem.nodePath();
        boolean dictSort = this.dictSort.isSelected();
        String properties = ZKNodeUtil.DATA_PROPERTIES + ZKNodeUtil.STAT_PROPERTIES;
        // 开始处理
        this.exportMsg.clear();
        this.stateManager.disable();
        this.stage.appendTitle("===导出执行中===");
        // 适用过滤
        if (this.applyFilter.isSelected()) {
            this.filters = this.filterStore.loadEnable();
        }
        // 执行导出
        this.exportTask = ThreadUtil.start(() -> {
            try {
                this.stopExportBtn.enable();
                // 初始化连接
                if (!this.zkClient.isConnected()) {
                    this.updateStatus("连接初始化...");
                    this.zkClient.start();
                    if (!this.zkClient.isConnected()) {
                        MessageBox.okToast("连接初始化失败！");
                        return;
                    }
                    this.updateStatus("导出执行中...");
                }
                // 获取节点
                List<ZKNode> zkNodes = new ArrayList<>();
                this.getNodeAll(this.exportPath, properties, zkNodes);
                // 取消操作
                if (ThreadUtil.isInterrupted(this.exportTask)) {
                    StaticLog.warn("export cancel!");
                    return;
                }
                // 排除临时节点
                zkNodes = zkNodes.parallelStream().filter(ZKNode::persistent).collect(Collectors.toList());
                if (CollUtil.isEmpty(zkNodes)) {
                    MessageBox.okToast("获取数据失败或数据为空！");
                    return;
                }
                // 节点按词典顺序排序
                if (dictSort) {
                    zkNodes.sort(ZKNode::compareTo);
                }
                // 判断是否json格式
                boolean isJSON = this.format.getSelectedIndex() == 0;
                // 导出内容
                String exportData;
                // 文件格式
                FileChooser.ExtensionFilter extensionFilter;
                // 处理名称
                String fileName = "ZK连接-" + this.zkClient.zkInfo().getName() + "-导出数据";
                if (isJSON) {
                    boolean prettyFormat = this.pretty.getSelectedIndex() == 0;
                    exportData = ZKExportUtil.nodesToJSON(zkNodes, CharsetUtil.defaultCharsetName(), prettyFormat);
                    extensionFilter = new FileChooser.ExtensionFilter("JSON files", "*.json");
                    fileName += ".json";
                } else {
                    // 前缀
                    String prefixVal = StrUtil.equalsAny(this.prefix.getValue(), "set", "create") ? this.prefix.getValue() : "";
                    exportData = ZKExportUtil.nodesToTxt(zkNodes, CharsetUtil.defaultCharsetName(), prefixVal);
                    extensionFilter = new FileChooser.ExtensionFilter("TXT files", "*.txt");
                    fileName += ".txt";
                }
                // 收尾工作
                this.updateStatus("处理文件中...");
                // this.exportMsg.waitTextExpend();
                File file = FileChooserUtil.save("导出zk数据", fileName, new FileChooser.ExtensionFilter[]{extensionFilter});
                // 保存文件
                if (file != null) {
                    FileUtil.writeUtf8String(exportData, file);
                    this.updateStatus("文件保存成功");
                    MessageBox.okToast("导出数据成功！");
                } else {
                    this.updateStatus("文件保存取消");
                }
            } catch (Exception e) {
                if (e.getClass().isAssignableFrom(InterruptedException.class)) {
                    this.updateStatus("数据导出取消");
                    MessageBox.okToast("导出数据取消！");
                } else {
                    e.printStackTrace();
                    this.updateStatus("数据导出失败");
                    MessageBox.warn("导出数据失败！");
                }
            } finally {
                // 结束处理
                this.stateManager.enable();
                this.stopExportBtn.disable();
                this.stage.restoreTitle();
                SystemUtil.gcLater();
            }
        });
    }

    /**
     * 结束导出
     */
    @FXML
    private void stopExport() {
        ThreadUtil.interrupt(this.exportTask);
        this.exportTask = null;
    }

    @Override
    protected void bindListeners() {
        this.stage.hideOnEscape();
        // 格式变化处理
        this.format.selectedIndexChanged((observableValue, number, t1) -> {
            if (t1.intValue() == 0) {
                this.prefixPane.disappear();
                this.prettyPane.display();
            } else {
                this.prefixPane.display();
                this.prettyPane.disappear();
            }
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
//        this.dictSort.setSelected(true);
        TreeItem<?> item = this.getStageProp("zkItem");
        if (item instanceof ZKNodeTreeItem treeItem) {
            this.zkClient = this.getStageProp("zkClient");
            this.exportPath = treeItem.nodePath();
            this.nodePath.setText(treeItem.decodeNodePath());
        } else if (item instanceof ZKConnectTreeItem treeItem) {
            this.nodePathBox.managedBindVisible();
            this.zkClient = new ZKClient(treeItem.value());
            this.exportPath = "/";
            this.nodePathBox.disappear();
        }
        // // 初始化字符集
        // this.charset.select(this.zkClient.getCharset());
        this.stage.hideOnEscape();
    }

    @Override
    public void onStageHidden(WindowEvent event) {
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
            if (ThreadUtil.isInterrupted(this.exportTask)) {
                return;
            }
            // 获取节点
            ZKNode zkNode = ZKNodeUtil.getNode(this.zkClient, path, properties);
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
                List<String> subs = this.zkClient.getChildren(path);
                // 递归获取节点
                for (String sub : subs) {
                    if (!ThreadUtil.isInterrupted(this.exportTask)) {
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
            msg = "导出节点：" + path + " 成功";
        } else if (status == 2) {
            msg = "导出节点：" + path + " 已忽略，此节点适用过滤配置";
        } else {
            msg = "导出节点：" + path + " 失败";
            if (ex != null) {
                msg += "，错误信息：" + ZKExceptionParser.INSTANCE.apply(ex);
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
}
