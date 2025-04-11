package cn.oyzh.easyzk.tabs.node;

import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.system.OSUtil;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.util.TextUtil;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.auth.ZKAuthAuthedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeAddedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeChangedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeCreatedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeRemovedEvent;
import cn.oyzh.easyzk.filter.ZKNodeFilterTextField;
import cn.oyzh.easyzk.filter.ZKNodeFilterTypeComboBox;
import cn.oyzh.easyzk.popups.ZKNodeQRCodePopupController;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeView;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.combobox.CharsetComboBox;
import cn.oyzh.fx.gui.svg.pane.CollectSVGPane;
import cn.oyzh.fx.gui.svg.pane.SortSVGPane;
import cn.oyzh.fx.gui.tabs.ParentTabController;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.gui.tabs.SubTabController;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.chooser.FXChooser;
import cn.oyzh.fx.plus.chooser.FileChooserHelper;
import cn.oyzh.fx.plus.controls.box.FXHBox;
import cn.oyzh.fx.plus.controls.box.FXVBox;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FXTabPane;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyHandler;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.node.NodeWidthResizer;
import cn.oyzh.fx.plus.thread.RenderService;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTypeComboBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * zk节点data组件
 *
 * @author oyzh
 * @since 2025/04/11
 */
public class ZKNodeDataTabController extends SubTabController {

    /**
     * 内容过滤组件
     */
    @FXML
    private ClearableTextField dataSearch;

    /**
     * 右侧zk权限视图切换按钮
     */
    @FXML
    private CharsetComboBox charset;

    /**
     * 数据大小
     */
    @FXML
    private FXText dataSize;

    /**
     * 加载耗时
     */
    @FXML
    private FXText loadTime;

    /**
     * zk数据保存
     */
    @FXML
    private SVGGlyph dataSave;

    /**
     * zk数据撤销
     */
    @FXML
    private SVGGlyph dataUndo;

    /**
     * zk数据重做
     */
    @FXML
    private SVGGlyph dataRedo;

    /**
     * 右侧zk数据
     */
    @FXML
    private RichDataTextAreaPane nodeData;

    /**
     * 格式
     */
    @FXML
    protected RichDataTypeComboBox format;

    /**
     * 数据面板
     */
    @FXML
    private FXTab dataTab;

    /**
     * 复制节点路径及数据
     */
    @FXML
    private void copyNode() {
        try {
            byte[] bytes = this.activeItem().getData();
            String data = this.activeItem().decodeNodePath() + " " + new String(bytes);
            ClipboardUtil.setStringAndTip(data);
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制节点路径
     */
    @FXML
    private void copyNodePath() {
        ClipboardUtil.setStringAndTip(this.activeItem().decodeNodePath());
    }

    /**
     * 保存为二进制文件
     */
    @FXML
    private void saveBinaryFile() {
        try {
            File file = FileChooserHelper.save(I18nHelper.saveFile(), "", FXChooser.allExtensionFilter());
            if (file != null) {
                FileUtil.writeBytes(this.activeItem().getNodeData(), file);
                MessageBox.info(I18nHelper.operationSuccess());
            }
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 刷新zk节点数据
     */
    @FXML
    private void reloadData() {
        // 放弃保存
        if (this.activeItem().isDataUnsaved() && !MessageBox.confirm(I18nHelper.unsavedAndContinue())) {
            return;
        }
        // 刷新数据
        try {
            this.activeItem().refreshData();
            // 数据变更
            this.showData();
            // 刷新tab颜色
            this.flushTabGraphicColor();
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 保存节点数据
     */
    @FXML
    private void saveNodeData() {
        if (this.activeItem().isDataTooBig()) {
            MessageBox.warn(I18nHelper.dataTooLarge());
            return;
        }
        // 保存数据
        if (!this.activeItem().isDataUnsaved()) {
            return;
        }
        // 保存数据
        RenderService.submit(() -> {
            // 保存数据
            if (this.activeItem().saveData()) {
                // 禁用图标
                this.dataSave.disable();
                // 刷新数据大小
                this.flushDataSize();
                // 刷新tab颜色
                this.flushTabGraphicColor();
            }
        });
    }

    /**
     * 数据撤销
     */
    @FXML
    private void dataUndo() {
        this.nodeData.undo();
        this.nodeData.requestFocus();
    }

    /**
     * 数据重做
     */
    @FXML
    private void dataRedo() {
        this.nodeData.redo();
        this.nodeData.requestFocus();
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.nodeData.paste();
        this.nodeData.requestFocus();
    }

    /**
     * 清空数据
     */
    @FXML
    private void clearData() {
        this.nodeData.clear();
        this.nodeData.requestFocus();
    }

    /**
     * zk节点转二维码
     */
    @FXML
    private void node2QRCode(MouseEvent event) {
        try {
            PopupAdapter adapter = PopupManager.parsePopup(ZKNodeQRCodePopupController.class);
            adapter.setProp("zkNode", this.activeItem().value());
            adapter.setProp("nodeData", this.nodeData.getTextTrim());
            adapter.showPopup((Node) event.getSource());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 认证节点
     */
    @FXML
    private void authNode() {
        this.activeItem().authNode();
    }

    /**
     * zk数据控件按键事件
     *
     * @param e 事件
     */
    @FXML
    private void onNodeDataKeyPressed(KeyEvent e) {
        // 保存节点数据
        if (KeyboardUtil.isCtrlS(e)) {
            this.saveNodeData();
            e.consume();
        }
    }

    /**
     * 显示历史
     */
    @FXML
    private void showHistory() {
        if (this.activeItem() != null) {
            ZKEventUtil.historyShow(this.activeItem());
        }
    }

    /**
     * 显示数据
     */
    protected void showData() {
        // 检测数据是否太大
        if (this.activeItem().isDataTooBig()) {
            this.nodeData.clear();
            this.nodeData.disable();
            NodeGroupUtil.disable(this.dataTab, "dataToBig");
            // 异步处理，避免阻塞主程序
            TaskManager.startDelay(() -> {
                if (MessageBox.confirm(ZKI18nHelper.nodeTip7())) {
                    this.saveBinaryFile();
                }
            }, 10);
            return;
        }
        NodeGroupUtil.enable(this.dataTab, "dataToBig");
        byte[] bytes = this.activeItem().getData();
        // 转换编码
        bytes = TextUtil.changeCharset(bytes, Charset.defaultCharset(), this.charset.getCharset());
        // 显示检测后的数据
        RichDataType dataType = this.nodeData.showDetectData(new String(bytes, this.charset.getCharset()));
        // 选中格式
        this.format.selectObj(dataType);
    }

    /**
     * 显示数据
     *
     * @param dataType 数据类型
     */
    protected void showData(RichDataType dataType) {
        byte[] bytes = this.activeItem().getData();
        bytes = TextUtil.changeCharset(bytes, Charset.defaultCharset(), this.charset.getCharset());
        this.nodeData.showData(dataType, bytes);
    }

    /**
     * 初始化数据
     */
    public void initData() {
        // 显示数据
        this.showData();
        // 刷新数据大小
        this.flushDataSize();
        // 遗忘历史
        this.nodeData.forgetHistory();
        // 按钮处理
        this.dataUndo.disable();
        this.dataRedo.disable();
        this.dataSave.setDisable(!this.activeItem().isDataUnsaved());
        // 加载耗时处理
        this.loadTime.text(I18nHelper.cost() + " : " + this.activeItem().loadTime() + "ms");
    }

    /**
     * 刷新数据大小
     */
    private void flushDataSize() {
        // 数据大小处理
        this.dataSize.text(I18nHelper.size() + " : " + this.activeItem().dataSizeInfo());
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // undo监听
        this.nodeData.undoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataUndo.setDisable(!t1));
        // redo监听
        this.nodeData.redoableProperty().addListener((observableValue, aBoolean, t1) -> this.dataRedo.setDisable(!t1));
        // 字符集选择事件
        this.charset.selectedItemChanged((t3, t2, t1) -> this.showData());
        // 节点内容过滤
        this.dataSearch.addTextChangeListener((observable, oldValue, newValue) -> this.nodeData.setHighlightText(newValue));
        // 格式监听
        this.format.selectedItemChanged((t1, t2, t3) -> {
            if (this.format.isStringFormat()) {
                this.showData(RichDataType.STRING);
                this.nodeData.setEditable(true);
            } else if (this.format.isJsonFormat()) {
                this.showData(RichDataType.JSON);
                this.nodeData.setEditable(true);
            } else if (this.format.isXmlFormat()) {
                this.showData(RichDataType.XML);
                this.nodeData.setEditable(true);
            } else if (this.format.isHtmlFormat()) {
                this.showData(RichDataType.HTML);
                this.nodeData.setEditable(true);
            } else if (this.format.isBinaryFormat()) {
                this.showData(RichDataType.BINARY);
                this.nodeData.setEditable(false);
            } else if (this.format.isHexFormat()) {
                this.showData(RichDataType.HEX);
                this.nodeData.setEditable(false);
            } else if (this.format.isRawFormat()) {
                this.showData(RichDataType.RAW);
                this.nodeData.setEditable(this.nodeData.getRealType() == RichDataType.STRING);
            }
        });
        // 节点内容变更
        this.nodeData.addTextChangeListener((observable, oldValue, newValue) -> {
            this.dataSave.enable();
            if (this.activeItem() != null) {
                byte[] bytes = newValue == null ? new byte[]{} : newValue.getBytes(this.charset.getCharset());
                this.activeItem().nodeData(bytes);
                this.flushTabGraphicColor();
            }
        });
    }

    /**
     * 设置数据高亮
     *
     * @param highlight 高亮内容
     */
    public void setDataHighlight(String highlight) {
        if (highlight != null) {
            this.nodeData.setHighlightText(highlight);
        } else {
            this.nodeData.setHighlightText(this.dataSearch.getTextTrim());
        }
    }

    private ZKNodeTreeItem activeItem() {
        return this.parent().getActiveItem();
    }

    @Override
    public ZKNodeTabController parent() {
        return (ZKNodeTabController) super.parent();
    }

}