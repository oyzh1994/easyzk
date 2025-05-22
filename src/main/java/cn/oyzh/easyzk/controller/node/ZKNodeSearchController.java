//package cn.oyzh.easyzk.controller.node;
//
//import cn.oyzh.common.util.StringUtil;
//import cn.oyzh.easyzk.domain.ZKConnect;
//import cn.oyzh.easyzk.event.ZKEventUtil;
//import cn.oyzh.easyzk.event.search.ZKSearchCloseEvent;
//import cn.oyzh.easyzk.event.search.ZKSearchCompleteEvent;
//import cn.oyzh.easyzk.search.ZKSearchParam;
//import cn.oyzh.event.EventSubscribe;
//import cn.oyzh.fx.gui.text.field.ClearableTextField;
//import cn.oyzh.fx.plus.FXConst;
//import cn.oyzh.fx.plus.controller.StageController;
//import cn.oyzh.fx.plus.controls.button.FXCheckBox;
//import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
//import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
//import cn.oyzh.fx.plus.window.FXStageStyle;
//import cn.oyzh.fx.plus.window.StageAttribute;
//import javafx.fxml.FXML;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.stage.WindowEvent;
//
//
///**
// * zk节点搜索业务
// *
// * @author oyzh
// * @since 2025/01/17
// */
//@StageAttribute(
//        resizable = false,
//        value = FXConst.FXML_PATH + "node/zkNodeSearch.fxml"
//)
//public class ZKNodeSearchController extends StageController {
//
//    @FXML
//    private ClearableTextField keyword;
//
//    @FXML
//    private FXCheckBox searchPath;
//
//    @FXML
//    private FXCheckBox searchData;
//
//    @FXML
//    private FXCheckBox matchCase;
//
//    @FXML
//    private FXCheckBox matchFull;
//
//    @FXML
//    private SVGGlyph next;
//
//    @FXML
//    private SVGGlyph prev;
//
//    private ZKConnect zkConnect;
//
//    @Override
//    public void onWindowShown(WindowEvent event) {
//        this.zkConnect = this.getProp("zkConnect");
//        this.stage.hideOnEscape();
//        super.onWindowShown(event);
//    }
//
//    @Override
//    public void onWindowHidden(WindowEvent event) {
//        super.onWindowHidden(event);
//        ZKEventUtil.searchFinish(this.zkConnect);
//    }
//
//    @Override
//    public String getViewTitle() {
//        return I18nResourceBundle.i18nString("zk.title.node.search");
//    }
//
//    @Override
//    protected void bindListeners() {
//        super.bindListeners();
//        this.prev.disableProperty().bind(this.next.disableProperty());
//        this.keyword.addTextChangeListener((observable, oldValue, newValue) -> {
//            if (StringUtil.isBlank(newValue)) {
//                this.next.disable();
//            } else {
//                this.next.enable();
//            }
//        });
//    }
//
//    private ZKSearchParam getParam() {
//        ZKSearchParam param = new ZKSearchParam();
//        param.setKeyword(this.keyword.getText());
//        param.setMatchCase(this.matchCase.isSelected());
//        param.setMatchFull(this.matchFull.isSelected());
//        param.setSearchPath(this.searchPath.isSelected());
//        param.setSearchData(this.searchData.isSelected());
//        return param;
//    }
//
//    @FXML
//    private void keywordKeyPressed(KeyEvent event) {
//        if (event.getCode() == KeyCode.ENTER && event.isAltDown()) {
//            this.searchPrev();
//        } else if (event.getCode() == KeyCode.ENTER) {
//            this.searchNext();
//        }
//    }
//
//    @FXML
//    private void searchPrev() {
//        ZKSearchParam param = getParam();
//        param.setAction("prev");
//        this.next.disable();
//        ZKEventUtil.searchTrigger(param, this.zkConnect);
//    }
//
//    @FXML
//    private void searchNext() {
//        ZKSearchParam param = getParam();
//        param.setAction("next");
//        this.next.disable();
//        ZKEventUtil.searchTrigger(param, this.zkConnect);
//    }
//
//    @EventSubscribe
//    public void onSearchComplete(ZKSearchCompleteEvent event) {
//        if (event.data() == this.zkConnect) {
//            this.next.enable();
//            this.keyword.requestFocus();
//        }
//    }
//
//    @EventSubscribe
//    public void onSearchClose(ZKSearchCloseEvent event) {
//        if (event.data() == this.zkConnect) {
//          this.closeWindow();
//        }
//    }
//}
