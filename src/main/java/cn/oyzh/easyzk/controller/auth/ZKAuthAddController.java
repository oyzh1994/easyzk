package cn.oyzh.easyzk.controller.auth;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.BaseResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;


/**
 * zk认证信息新增业务
 *
 * @author oyzh
 * @since 2022/12/22
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.APPLICATION_MODAL,
        value = ZKConst.FXML_BASE_PATH + "auth/zkAuthAdd.fxml"
)
public class ZKAuthAddController extends Controller {

    /**
     * 用户名
     */
    @FXML
    private ClearableTextField user;

    /**
     * 密码
     */
    @FXML
    private ClearableTextField password;

    /**
     * 认证储存
     */
    private final ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    /**
     * 新增认证信息
     */
    @FXML
    private void addAuth() {
        try {
            String user = this.user.getText().trim();
            String password = this.password.getText().trim();
            if (StrUtil.isBlank(user)) {
                MessageBox.tipMsg(BaseResourceBundle.getBaseString("base.userNameNotEmpty"), this.user);
                return;
            }
            if (StrUtil.isBlank(password)) {
                MessageBox.tipMsg(BaseResourceBundle.getBaseString("base.passwordNotEmpty"), this.password);
                return;
            }
            if (this.authStore.exist(user, password)) {
                MessageBox.warn(BaseResourceBundle.getBaseString("base.contentAlreadyExists"));
                return;
            }
            ZKAuth auth = new ZKAuth(user, password);
            if (this.authStore.add(auth)) {
                ZKEventUtil.authAdded(auth);
                MessageBox.okToast(BaseResourceBundle.getBaseString("base.actionSuccess"));
                this.closeStage();
            } else {
                MessageBox.warn(BaseResourceBundle.getBaseString("base.actionFail"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String i18nId() {
        return "auth.add";
    }
}
