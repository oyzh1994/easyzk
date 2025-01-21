package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.fx.ZKDataTextArea;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.keyboard.KeyboardUtil;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTypeComboBox;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryDataTabController extends DynamicTabController {

    private String path;

    private ZKClient zkClient;

    @FXML
    private SVGGlyph save;

    @FXML
    private SVGGlyph undo;

    @FXML
    private SVGGlyph redo;

    @FXML
    private ZKDataTextArea data;

    @FXML
    private RichDataTypeComboBox format;

    public void init(String path, byte[] data, ZKClient zkClient) {
        this.path = path;
        this.zkClient = zkClient;
        // 处理数据
        data = data == null ? new byte[]{} : data;
        // 显示检测后的数据
        RichDataType dataType = this.data.showDetectData(new String(data));
        // 遗忘历史
        this.data.forgetHistory();
        // 选中格式
        this.format.selectObj(dataType);
        // 绑定监听器
        this.data.addTextChangeListener((observable, oldValue, newValue) -> this.save.enable());
        this.data.undoableProperty().addListener((observable, oldValue, newValue) -> this.undo.setDisable(!newValue));
        this.data.redoableProperty().addListener((observable, oldValue, newValue) -> this.redo.setDisable(!newValue));
        // 格式监听
        this.format.selectedItemChanged((t1, t2, t3) -> {
            if (this.format.isStringFormat()) {
                this.data.showStringData(this.data.getText());
                this.data.setEditable(true);
            } else if (this.format.isJsonFormat()) {
                this.data.showJsonData(this.data.getText());
                this.data.setEditable(true);
            } else if (this.format.isBinaryFormat()) {
                this.data.showBinaryData(this.data.getText());
                this.data.setEditable(false);
            } else if (this.format.isHexFormat()) {
                this.data.showHexData(this.data.getText());
                this.data.setEditable(false);
            } else if (this.format.isRawFormat()) {
                this.data.showRawData(this.data.getText());
                this.data.setEditable(this.data.getRealType() == RichDataType.STRING);
            }
        });
    }

    @FXML
    private void save() {
        try {
            this.save.disable();
            this.zkClient.setData(this.path, this.data.getText());
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @FXML
    private void undo() {
        this.data.undo();
        this.data.requestFocus();
    }

    @FXML
    private void redo() {
        this.data.undo();
        this.data.requestFocus();
    }

    @FXML
    private void onDataKeyPressed(KeyEvent event) {
        if (KeyboardUtil.isCtrlS(event)) {
            this.save();
        }
    }
}