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
        modality = Modality.WINDOW_MODAL,
        stageStyle = FXStageStyle.UTILITY,
        value = FXConst.FXML_PATH + "node/zkNodeQRCode.fxml"
)
public class ZKQRCodeNodeController extends StageController {

    /**
     * 二维码图片
     */
    @FXML
    private ImageView qrcode;

    @Override
    public void onWindowShown(WindowEvent event) {
        this.stage.hideOnEscape();
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
            int codeW = (int) this.qrcode.getFitWidth();
            int codeH = (int) this.qrcode.getFitHeight();
            BufferedImage source = QRCodeUtil.createImage(builder.toString(), "utf-8", codeW, codeH);
            String filePath = ResourceUtil.getResource(ZKConst.ICON_PATH).getFile();
            File iconFile = new File(filePath);
            QRCodeUtil.insertImage(source, iconFile, 60, 60, true);
            this.qrcode.setImage(FXUtil.toImage(source));
        } catch (Exception ex) {
            this.closeWindow();
            ex.printStackTrace();
            JulLog.warn("initQRCode error, ex:{}", ex.getMessage());
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.node.qrcode");
    }
}
