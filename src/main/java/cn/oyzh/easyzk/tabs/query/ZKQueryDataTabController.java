package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.dto.ZKQueryParam;
import cn.oyzh.easyzk.dto.ZKQueryResult;
import cn.oyzh.easyzk.fx.ZKDataTextArea;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

/**
 * zk更新日志tab内容组件
 *
 * @author oyzh
 * @since 2024/04/07
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

    public void init(String path, byte[] data, ZKClient zkClient) {
        this.path = path;
        this.zkClient = zkClient;
        this.data.setText(data == null ? "" : new String(data));
        this.data.addTextChangeListener((observable, oldValue, newValue) -> this.save.enable());
        this.undo.disableProperty().bind(this.data.undoableProperty());
        this.redo.disableProperty().bind(this.data.redoableProperty());
    }

    @FXML
    private void save() {
        try {
            this.zkClient.setData(this.path, this.data.getText());
            this.save.disable();
            this.data.requestFocus();
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
}