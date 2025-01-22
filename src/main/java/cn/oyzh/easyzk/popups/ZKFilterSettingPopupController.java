package cn.oyzh.easyzk.popups;

import cn.oyzh.easyzk.filter.ZKNodeFilterParam;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.PopupController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.PopupAdapter;
import cn.oyzh.fx.plus.window.PopupAttribute;
import javafx.fxml.FXML;
import javafx.stage.WindowEvent;

import static atlantafx.base.controls.Popover.ArrowLocation.BOTTOM_LEFT;
import static javafx.stage.PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT;

/**
 * 过滤设置弹窗
 *
 * @author oyzh
 * @since 2025/01/22
 */
@PopupAttribute(
        value = FXConst.POPUP_PATH + "zkFilterSettingPopup.fxml",
        arrowLocation = BOTTOM_LEFT,
        anchorLocation = CONTENT_BOTTOM_LEFT
)
public class ZKFilterSettingPopupController extends PopupController {

    @FXML
    private FXCheckBox searchPath;

    @FXML
    private FXCheckBox searchData;

    @FXML
    private FXCheckBox matchCase;

    @FXML
    private FXCheckBox matchFull;

    /**
     * 应用
     */
    @FXML
    private void apply() {
        try {
            ZKNodeFilterParam filterParam = new ZKNodeFilterParam();
            filterParam.setMatchCase(this.matchCase.isSelected());
            filterParam.setMatchFull(this.matchFull.isSelected());
            filterParam.setSearchPath(this.searchPath.isSelected());
            filterParam.setSearchData(this.searchData.isSelected());
            this.submit(filterParam);
            this.closeWindow();
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
    }

    /**
     * 关闭
     */
    @FXML
    private void close() {
        this.closeWindow();
    }

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        ZKNodeFilterParam filterParam = this.getWindowProp("filterParam");
        if (filterParam != null) {
            this.matchCase.setSelected(filterParam.isMatchCase());
            this.matchFull.setSelected(filterParam.isMatchFull());
            this.searchPath.setSelected(filterParam.isSearchPath());
            this.searchData.setSelected(filterParam.isSearchData());
        }
    }
}