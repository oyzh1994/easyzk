package cn.oyzh.easyzk.controller.node;

import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.log.JulLog;
import cn.oyzh.fx.common.util.ResourceUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;


/**
 * zk数据二维码业务
 *
 * @author oyzh
 * @since 2022/08/23
 */
@StageAttribute(
        resizeable = false,
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        stageStyle = StageStyle.DECORATED,
        value = ZKConst.FXML_BASE_PATH + "node/zkNodeQRCode.fxml"
)
public class ZKNodeQRCodeController extends StageController {

    /**
     * 二维码图片
     */
    @FXML
    private ImageView qrcode;

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.hideOnEscape();
        this.initQRCode();
    }

    /**
     * 初始化二维码
     */
    private void initQRCode() {
        ByteArrayInputStream bais = null;
        try {
            JulLog.info("read icon begin.");
            // icon图片
            var iconUrl = ResourceUtil.getResource(ZKConst.ICON_PATH);
            var icon = ImageIO.read(iconUrl);
            JulLog.info("read icon finish.");
            ZKNode zkNode = this.getWindowProp("zkNode");
            String nodeData = this.getWindowProp("nodeData");
            StringBuilder builder = new StringBuilder();
            builder.append(I18nHelper.nodePath()).append(": ")
                    .append(zkNode.decodeNodePath()).append("\n")
                    .append(I18nHelper.nodeData()).append(": ")
                    .append(nodeData);
//                    .append(nodeData).append("\n")
//                    .append(I18nHelper.nodeType()).append(": ")
//                    .append(zkNode.ephemeral() ? I18nResourceBundle.i18nString("base.ephemeralNode") : I18nResourceBundle.i18nString("base.persistentNode"));
//            if (zkNode.stat() != null) {
//                builder.append("\n")
//                        .append(I18nHelper.createTime()).append(": ").append(Const.DATE_FORMAT.format(zkNode.stat().getCtime())).append("\n")
//                        .append(I18nHelper.updateTime()).append(": ").append(Const.DATE_FORMAT.format(zkNode.stat().getMtime())).append("\n");
//            }
            JulLog.info("generate qrcode begin.");
            QrConfig config = new QrConfig((int) this.qrcode.getFitWidth(), (int) this.qrcode.getFitHeight());
            config.setImg(icon);
            byte[] bytes = QrCodeUtil.generatePng(builder.toString(), config);
            bais = new ByteArrayInputStream(bytes);
            this.qrcode.setImage(new Image(bais));
            JulLog.info("generate qrcode finish size:{}", bais.available());
        } catch (Exception ex) {
            this.closeWindow();
            ex.printStackTrace();
            JulLog.warn("initQRCode error, ex:{}", ex.getMessage());
            if (ex.getMessage().contains("Data too big")) {
                MessageBox.warn(I18nHelper.dataTooLarge());
            } else {
                MessageBox.exception(ex, I18nHelper.operationFail());
            }
        } finally {
            IoUtil.close(bais);
        }
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.node.qrcode");
    }
}
