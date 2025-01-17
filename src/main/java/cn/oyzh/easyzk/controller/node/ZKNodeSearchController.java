package cn.oyzh.easyzk.controller.node;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.qrcode.QRCodeUtil;
import cn.oyzh.common.util.ResourceUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * zk数据二维码业务
 *
 * @author oyzh
 * @since 2022/08/23
 */
@StageAttribute(
        resizable = false,
        stageStyle = FXStageStyle.UTILITY,
        value = FXConst.FXML_PATH + "node/zkNodeSearch.fxml"
)
public class ZKNodeSearchController extends StageController {

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.node.search");
    }
}
