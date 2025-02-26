package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.query.ZKQueryParam;
import cn.oyzh.easyzk.query.ZKQueryResult;
import cn.oyzh.fx.gui.tabs.RichTabController;
import cn.oyzh.fx.gui.text.area.ReadOnlyTextArea;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryMsgTabController extends RichTabController {

    @FXML
    private ReadOnlyTextArea msg;

    public void init(ZKQueryParam param, ZKQueryResult result) {
        this.msg.appendLine(param.getContent());
        this.msg.appendLine("> " + result.getMessage());
        this.msg.appendLine("> " + I18nHelper.cost() + ": " + result.costSeconds());
//        if (result.isSuccess() && param.isGetAllChildrenNumber()) {
//            this.msg.appendLine("> " + I18nHelper.nodeCount() + ": " + result.getResult());
//        }
    }
}