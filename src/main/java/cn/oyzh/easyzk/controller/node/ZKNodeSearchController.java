package cn.oyzh.easyzk.controller.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.search.ZKSearchCompleteEvent;
import cn.oyzh.easyzk.event.search.ZKSearchTriggerEvent;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.WindowEvent;


/**
 * zk节点搜索业务
 *
 * @author oyzh
 * @since 2025/01/17
 */
@StageAttribute(
        resizable = false,
        stageStyle = FXStageStyle.UTILITY,
        value = FXConst.FXML_PATH + "node/zkNodeSearch.fxml"
)
public class ZKNodeSearchController extends StageController {

    @FXML
    private ClearableTextField keyword;

    @FXML
    private FXCheckBox searchPath;

    @FXML
    private FXCheckBox searchData;

    @FXML
    private FXCheckBox loadAll;

    private ZKConnect zkConnect;

    @Override
    public void onStageShown(WindowEvent event) {
        this.zkConnect = this.getWindowProp("zkConnect");
        this.stage.hideOnEscape();
    }

    @Override
    public void onWindowHidden(WindowEvent event) {
        super.onWindowHidden(event);
        ZKEventUtil.searchFinish(this.zkConnect);
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.node.search");
    }

    private ZKSearchParam getParam() {
        ZKSearchParam param = new ZKSearchParam();
        param.setKeyword(keyword.getText());
        param.setSearchPath(searchPath.isSelected());
        param.setSearchData(searchData.isSelected());
        param.setLoadAll(loadAll.isSelected());
        return param;
    }

    @FXML
    private void keywordKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
            this.searchPrev();
        } else if (event.getCode() == KeyCode.ENTER) {
            this.searchNext();
        }
    }

    @FXML
    private void searchPrev() {
        ZKSearchParam param = getParam();
        param.setAction("prev");
        ZKEventUtil.searchTrigger(param, this.zkConnect);
        this.disable();
        this.keyword.requestFocus();
    }

    @FXML
    private void searchNext() {
        ZKSearchParam param = getParam();
        param.setAction("next");
        ZKEventUtil.searchTrigger(param, this.zkConnect);
        this.disable();
        this.keyword.requestFocus();
    }

    @EventSubscribe
    public void onSearchComplete(ZKSearchCompleteEvent event) {
        if (event.data() == this.zkConnect) {
            this.enable();
        }
    }
}
