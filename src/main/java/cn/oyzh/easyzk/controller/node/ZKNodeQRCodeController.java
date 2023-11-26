package cn.oyzh.easyzk.controller.node;

import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.Const;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.ResourceUtil;
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
//@Slf4j
@StageAttribute(
        resizeable = false,
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        stageStyle = StageStyle.DECORATED,
        title = "可使用微信、QQ或其他工具扫描二维码获取节点信息",
        value = ZKConst.FXML_BASE_PATH + "node/zkNodeQRCode.fxml"
)
public class ZKNodeQRCodeController extends Controller {

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
//        ByteArrayOutputStream baos = null;
        try {
            StaticLog.info("read icon begin.");
            // icon图片
            var iconUrl = ResourceUtil.getResource(ZKConst.ICON_PATH);
            var icon = ImageIO.read(iconUrl);
            StaticLog.info("read icon finish.");
            ZKNode zkNode = this.getStageProp("zkNode");
            String nodeData = this.getStageProp("nodeData");
            StringBuilder builder = new StringBuilder();
            builder.append("节点路径: ").append(zkNode.decodeNodePath()).append("\n")
                    .append("节点数据: ").append(nodeData).append("\n")
                    .append("节点类型: ").append(zkNode.ephemeral() ? "临时节点" : "持久节点");
            if (zkNode.stat() != null) {
                builder.append("\n")
                        .append("创建时间: ").append(Const.DATE_FORMAT.format(zkNode.stat().getCtime())).append("\n")
                        .append("修改时间: ").append(Const.DATE_FORMAT.format(zkNode.stat().getMtime())).append("\n");
            }
            StaticLog.info("generate qrcode begin.");
            QrConfig config = new QrConfig((int) this.qrcode.getFitWidth(), (int) this.qrcode.getFitHeight());
            config.setImg(icon);
            byte[] bytes = QrCodeUtil.generatePng(builder.toString(), config);
//            BufferedImage source = QRCodeUtil.createImage(builder.toString(), (int) this.qrcode.getFitWidth(), (int) this.qrcode.getFitHeight());
//            QRCodeUtil.insertImage(source, icon);
//            baos = new ByteArrayOutputStream();
//            ImageIO.write(source, "png", baos);
//            bais = new ByteArrayInputStream(baos.toByteArray());
            bais = new ByteArrayInputStream(bytes);
            this.qrcode.setImage(new Image(bais));
            StaticLog.info("generate qrcode finish size:{}", bais.available());
        } catch (Exception ex) {
            this.closeStage();
            ex.printStackTrace();
            StaticLog.warn("initQRCode error, ex:{}", ex.getMessage());
            if (ex.getMessage().contains("Data too big")) {
                MessageBox.warn("节点数据太大，不支持生成二维码！");
            } else {
                MessageBox.exception(ex, "生成二维码异常");
            }
        } finally {
            IoUtil.close(bais);
//            IoUtil.close(baos);
        }
    }
}
