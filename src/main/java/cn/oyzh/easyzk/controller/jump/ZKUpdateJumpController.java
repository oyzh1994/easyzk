package cn.oyzh.easyzk.controller.jump;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKJumpConfig;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.fx.gui.combobox.SSHAuthTypeCombobox;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PasswordTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.gui.text.field.ReadOnlyTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.chooser.FXChooser;
import cn.oyzh.fx.plus.chooser.FileChooserHelper;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.validator.ValidatorUtil;
import cn.oyzh.fx.plus.window.FXStageStyle;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.ssh.domain.SSHConnect;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.io.File;

/**
 * ssh跳板编辑业务
 *
 * @author oyzh
 * @since 2025/05/20
 */
@StageAttribute(
        stageStyle = FXStageStyle.UNIFIED,
        modality = Modality.APPLICATION_MODAL,
        value = FXConst.FXML_PATH + "jump/zkUpdateJump.fxml"
)
public class ZKUpdateJumpController extends StageController {

    /**
     * 跳板名称
     */
    @FXML
    private ClearableTextField sshName;

    /**
     * ssh主机地址
     */
    @FXML
    private ClearableTextField sshHost;

    /**
     * ssh主机端口
     */
    @FXML
    private PortTextField sshPort;

    /**
     * ssh主机端口
     */
    @FXML
    private NumberTextField sshTimeout;

    /**
     * ssh主机用户
     */
    @FXML
    private ClearableTextField sshUser;

    /**
     * ssh主机密码
     */
    @FXML
    private PasswordTextField sshPassword;

    /**
     * ssh认证方式
     */
    @FXML
    private SSHAuthTypeCombobox sshAuthMethod;

    /**
     * ssh证书
     */
    @FXML
    private ReadOnlyTextField sshCertificate;

    /**
     * 跳板配置
     */
    private ZKJumpConfig config;

    /**
     * 是否启用
     */
    @FXML
    private FXToggleSwitch enable;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        if (!this.sshPort.validate() || !this.sshHost.validate()) {
            return null;
        }
        String hostIp = this.sshHost.getTextTrim();
        hostText = hostIp + ":" + this.sshPort.getValue();
        return hostText;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (!StringUtil.isBlank(host)) {
            // 创建ssh信息
            SSHConnect shellConnect = new SSHConnect();
            shellConnect.setHost(this.sshHost.getTextTrim());
            shellConnect.setPort(this.sshPort.getIntValue());
            // 认证信息
            shellConnect.setUser(this.sshUser.getTextTrim());
            shellConnect.setPassword(this.sshPassword.getPassword());
            shellConnect.setAuthMethod(this.sshAuthMethod.getAuthType());
            shellConnect.setCertificatePath(this.sshCertificate.getTextTrim());
            ZKConnectUtil.testSSHConnect(this.stage, shellConnect);
        }
    }

    /**
     * 修改跳板信息
     */
    @FXML
    private void update() {
        String name = this.sshName.getTextTrim();
        if (!this.sshName.validate()) {
            return;
        }
        String userName = this.sshUser.getTextTrim();
        if (!this.sshUser.validate()) {
            return;
        }
        String password = this.sshPassword.getPassword();
        if (this.sshAuthMethod.isPasswordAuth() && StringUtil.isBlank(password)) {
            ValidatorUtil.validFail(this.sshPassword);
            return;
        }
        String certificate = this.sshCertificate.getTextTrim();
        if (this.sshAuthMethod.isCertificateAuth() && StringUtil.isBlank(certificate)) {
            ValidatorUtil.validFail(this.sshCertificate);
            return;
        }
        try {
            int port = this.sshPort.getIntValue();
            String host = this.sshHost.getTextTrim();
            int timeout = this.sshTimeout.getIntValue();
            String authType = this.sshAuthMethod.getAuthType();
            this.config.setName(name);
            this.config.setPort(port);
            this.config.setHost(host);
            this.config.setUser(userName);
            this.config.setPassword(password);
            this.config.setAuthMethod(authType);
            this.config.setTimeout(timeout * 1000);
            this.config.setCertificatePath(certificate);
            this.config.setEnabled(this.enable.isSelected());
            this.closeWindow();
            // 设置数据
            this.setProp("jumpConfig", this.config);
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    @Override
    protected void bindListeners() {
        // ssh认证方式
        this.sshAuthMethod.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (this.sshAuthMethod.isPasswordAuth()) {
                this.sshPassword.display();
                NodeGroupUtil.disappear(this.stage, "sshCertificate");
            } else {
                this.sshPassword.disappear();
                NodeGroupUtil.display(this.stage, "sshCertificate");
            }
        });
    }

    @Override
    public void onWindowShown(WindowEvent event) {
        super.onWindowShown(event);
        this.config = this.getProp("config");
        this.sshName.setText(this.config.getName());
        this.sshHost.setText(this.config.getHost());
        this.sshUser.setText(this.config.getUser());
        this.sshPort.setValue(this.config.getPort());
        this.enable.setSelected(this.config.isEnabled());
        this.sshTimeout.setValue(this.config.getTimeout());
        this.sshPassword.setText(this.config.getPassword());
        this.sshCertificate.setText(this.config.getCertificatePath());
        if (this.config.isPasswordAuth()) {
            this.sshAuthMethod.selectFirst();
        } else {
            this.sshAuthMethod.selectLast();
        }
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.updateJumpHost();
    }

    /**
     * 选择ssh证书
     */
    @FXML
    private void chooseSSHCertificate() {
        File file = FileChooserHelper.choose(I18nHelper.pleaseSelectFile(), FXChooser.allExtensionFilter());
        if (file != null) {
            this.sshCertificate.setText(file.getPath());
        }
    }
}
