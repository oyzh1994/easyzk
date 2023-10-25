package cn.oyzh.easyzk.controller.node;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.Const;
import cn.oyzh.fx.common.util.QRCodeUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexImageView;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.util.ResourceUtil;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


/**
 * zk数据二维码业务
 *
 * @author oyzh
 * @since 2022/08/23
 */
@Slf4j
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
    private FlexImageView qrcode;

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.hideOnEscape();
        this.initQRCode();
    }

    /**
     * 初始化二维码
     */
    private void initQRCode() {
        try {
            log.info("read icon begin.");
            // icon图片
            var iconUrl = ResourceUtil.getResource(ZKConst.ICON_PATH);
            var icon = ImageIO.read(iconUrl);
            log.info("read icon finish.");
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
            log.info("generate qrcode begin.");
            BufferedImage source = QRCodeUtil.createImage(builder.toString(), 450, 450);
            QRCodeUtil.insertImage(source, icon);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(source, "png", baos);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            this.qrcode.setImage(new Image(bais));
            log.info("generate qrcode finish size:{}", bais.available());
            baos.close();
            bais.close();
        } catch (Exception ex) {
            this.closeStage();
            ex.printStackTrace();
            log.warn("initQRCode error, ex:{}", ex.getMessage());
            if (ex.getMessage().contains("Data too big")) {
                MessageBox.warn("节点数据太大，不支持生成二维码！");
            } else {
                MessageBox.exception(ex, "生成二维码异常");
            }
        }
    }
}
