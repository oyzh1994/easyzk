package cn.oyzh.easyzk.popups;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.qrcode.QRCodeUtil;
import cn.oyzh.common.util.ResourceUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.PopupController;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeUtil;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.PopupAttribute;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.awt.image.BufferedImage;
import java.io.File;

import static atlantafx.base.controls.Popover.ArrowLocation.BOTTOM_LEFT;
import static atlantafx.base.controls.Popover.ArrowLocation.TOP_CENTER;
import static javafx.stage.PopupWindow.AnchorLocation.CONTENT_BOTTOM_LEFT;
import static javafx.stage.PopupWindow.AnchorLocation.CONTENT_TOP_LEFT;


/**
 * zk数据二维码业务
 *
 * @author oyzh
 * @since 2024/02/21
 */
@PopupAttribute(
        value = FXConst.POPUP_PATH + "zkNodeQRCodePopup.fxml",
        arrowLocation = TOP_CENTER,
        anchorLocation = CONTENT_BOTTOM_LEFT
)
public class ZKNodeQRCodePopupController extends PopupController {

    /**
     * 二维码图片
     */
    @FXML
    private ImageView qrcode;

    @Override
    public void onWindowShowing(WindowEvent event) {
        super.onWindowShowing(event);
        this.initQRCode();
    }

    /**
     * 初始化二维码
     */
    private void initQRCode() {
        try {
            ZKNode zkNode = this.getWindowProp("zkNode");
            String nodeData = this.getWindowProp("nodeData");
            StringBuilder builder = new StringBuilder();
            builder.append(I18nHelper.nodePath()).append(": ")
                    .append(zkNode.decodeNodePath()).append("\n")
                    .append(I18nHelper.nodeData()).append(": ")
                    .append(nodeData);
            JulLog.info("generate qrcode begin.");
            int codeW = (int) NodeUtil.getWidth(this.qrcode);
            int codeH = (int) NodeUtil.getHeight(this.qrcode);
            BufferedImage source = QRCodeUtil.createImage(builder.toString(), "utf-8", codeW, codeH);
            this.qrcode.setImage(FXUtil.toImage(source));
        } catch (Exception ex) {
            this.closeWindow();
            ex.printStackTrace();
            JulLog.warn("initQRCode error, ex:{}", ex.getMessage());
            MessageBox.warn(I18nHelper.operationFail());
        }
    }
}
