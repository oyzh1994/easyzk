package cn.oyzh.easyzk.controller.auth;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
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
        title = "zk认证信息新增",
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
                MessageBox.tipMsg("用户名不能为空！", this.user);
                return;
            }
            if (StrUtil.isBlank(password)) {
                MessageBox.tipMsg("密码不能为空！", this.password);
                return;
            }
            if (this.authStore.exist(user, password)) {
                MessageBox.warn("此认证用户名、认证密码信息已存在！");
                return;
            }
            ZKAuth auth = new ZKAuth(user, password);
            if (this.authStore.add(auth)) {
                // ZKAuthUtil.fireAuthAddEvent(auth);
                ZKEventUtil.authAdded(auth);
                MessageBox.okToast("新增认证信息成功！");
                this.closeStage();
            } else {
                MessageBox.warn("新增认证信息失败！");
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
}
