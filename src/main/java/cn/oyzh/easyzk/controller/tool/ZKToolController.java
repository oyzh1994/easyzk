package cn.oyzh.easyzk.controller.tool;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
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
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "tool/zkTool.fxml"
)
public class ZKToolController extends StageController {

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
            if(StringUtil.isBlank(this.user.getText())){
                this.user.requestFocus();
                return;
            }
            if(StringUtil.isBlank(this.pwd.getText())){
                this.pwd.requestFocus();
                return;
            }
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
        MessageBox.info(I18nHelper.copySuccess());
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

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("zk.title.tool");
    }
}
