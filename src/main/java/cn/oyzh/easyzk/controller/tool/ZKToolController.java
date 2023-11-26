package cn.oyzh.easyzk.controller.tool;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * zk工具箱业务
 *
 * @author oyzh
 * @since 2023/11/09
 */
//@Slf4j
@StageAttribute(
        title = "zk工具箱",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "tool/zkTool.fxml"
)
public class ZKToolController extends Controller {

    /**
     * 用户
     */
    @FXML
    private ClearableTextField user;

    /**
     * 密码
     */
    @FXML
    private ClearableTextField pwd;

    /**
     * 摘要
     */
    @FXML
    private TextField digest;

    /**
     * 生成摘要
     */
    @FXML
    private void genDigest() {
        try {
            String digest1 = ZKAuthUtil.digest(this.user.getText(), this.pwd.getText());
            this.digest.setText(digest1);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 复制摘要
     */
    @FXML
    private void copyDigest() {
        this.digest.copy();
        MessageBox.info("已复制到剪贴板");
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        this.user.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为用户名密码
            if (t1 != null && t1.contains(":")) {
                this.user.setText(t1.split(":")[0]);
                this.pwd.setText(t1.split(":")[1]);
            } else {
                this.digest.clear();
            }
        });
        this.pwd.addTextChangeListener((observableValue, s, t1) -> this.digest.clear());
    }
}
