package cn.oyzh.easyzk.controller.auth;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKAuthStore2;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
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
public class ZKAuthAddController extends StageController {

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
    private final ZKAuthStore2 authStore = ZKAuthStore2.INSTANCE;

    /**
     * 新增认证信息
     */
    @FXML
    private void addAuth() {
        try {
            String user = this.user.getText().trim();
            String password = this.password.getText().trim();
            if (StringUtil.isBlank(user)) {
                MessageBox.tipMsg(I18nHelper.userNameCanNotEmpty(), this.user);
                return;
            }
            if (StringUtil.isBlank(password)) {
                MessageBox.tipMsg(I18nHelper.passwordCanNotEmpty(), this.password);
                return;
            }
            if (this.authStore.exist(user, password)) {
                MessageBox.warn(I18nHelper.contentAlreadyExists());
                return;
            }
            ZKAuth auth = new ZKAuth(user, password);
            if (this.authStore.replace(auth)) {
                ZKEventUtil.authAdded(auth);
                MessageBox.okToast(I18nHelper.operationSuccess());
                this.closeWindow();
            } else {
                MessageBox.warn(I18nHelper.operationFail());
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
    public String getViewTitle() {
        return I18nHelper.addAuth();
    }
}
