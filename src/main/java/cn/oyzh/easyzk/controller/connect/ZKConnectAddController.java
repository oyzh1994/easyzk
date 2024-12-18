package cn.oyzh.easyzk.controller.connect;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKConnectJdbcStore;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * 添加zk信息业务
 *
 * @author oyzh
 * @since 2020/9/15
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        iconUrl = ZKConst.ICON_PATH,
        value = ZKConst.FXML_BASE_PATH + "connect/zkConnectAdd.fxml"
)
public class ZKConnectAddController extends StageController {

    /**
     * 只读模式
     */
    @FXML
    private FXCheckBox readonly;

    /**
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

    /**
     * 备注
     */
    @FXML
    private FlexTextArea remark;

    /**
     * 监听节点
     */
    @FXML
    private FXCheckBox listen;

    /**
     * 兼容模式开关
     */
    @FXML
    private FXCheckBox compatibility;

    /**
     * 连接ip
     */
    @FXML
    private ClearableTextField hostIp;

    /**
     * 连接端口
     */
    @FXML
    private PortTextField hostPort;

    /**
     * 连接超时时间
     */
    @FXML
    private NumberTextField connectTimeOut;

    /**
     * 会话超时时间
     */
    @FXML
    private NumberTextField sessionTimeOut;

    /**
     * 开启ssh
     */
    @FXML
    private FXToggleSwitch sshForward;

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
    private ClearableTextField sshPassword;

    /**
     * ssh连接组件
     */
    @FXML
    private FlexHBox sshHostBox;

    /**
     * ssh认证组件
     */
    @FXML
    private FlexHBox sshAuthBox;

    // /**
    //  * ssh超时组件
    //  */
    // @FXML
    // private FlexHBox sshTimeoutBox;

    /**
     * 分组
     */
    private ZKGroup group;

    // /**
    //  * zk连接储存对象
    //  */
    // private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        // String host = this.host.getTextTrim();
        String hostIp = this.hostIp.getTextTrim();
        this.tabPane.select(0);
        if (!this.hostPort.validate()) {
            this.tabPane.select(0);
            return null;
        }
        if (!this.hostIp.validate()) {
            this.tabPane.select(0);
            return null;
        }
        hostText = hostIp + ":" + this.hostPort.getValue();
        return hostText;
    }

    /**
     * 获取ssh信息
     *
     * @return ssh连接信息
     */
    private ZKSSHConnect getSSHInfo() {
        ZKSSHConnect sshConnectInfo = new ZKSSHConnect();
        sshConnectInfo.setHost(this.sshHost.getText());
        sshConnectInfo.setUser(this.sshUser.getText());
        sshConnectInfo.setPassword(this.sshPassword.getText());
        sshConnectInfo.setPort(this.sshPort.getIntValue());
        sshConnectInfo.setTimeout(this.sshTimeout.getIntValue());
        return sshConnectInfo;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StringUtil.isBlank(host) || StringUtil.isBlank(host.split(":")[0])) {
            MessageBox.warn(I18nHelper.contentCanNotEmpty());
        } else {
            // 创建zk信息
            ZKConnect zkInfo = new ZKConnect();
            zkInfo.setHost(host);
            zkInfo.setConnectTimeOut(3);
            zkInfo.setSshForward(this.sshForward.isSelected());
            if (zkInfo.isSSHForward()) {
                zkInfo.setSshConnect(this.getSSHInfo());
            }
            ZKConnectUtil.testConnect(this.stage, zkInfo);
        }
    }

    /**
     * 添加zk信息
     */
    @FXML
    private void add() {
        String host = this.getHost();
        if (host == null) {
            return;
        }
        // 名称未填，则直接以host为名称
        if (StringUtil.isBlank(this.name.getTextTrim())) {
            this.name.setText(host.replace(":", "_"));
        }
        try {
            String name = this.name.getTextTrim();
            ZKConnect zkInfo = new ZKConnect();
            zkInfo.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            Number sessionTimeOut = this.sessionTimeOut.getValue();
            zkInfo.setHost(host);
            zkInfo.setSshConnect(this.getSSHInfo());
            zkInfo.setListen(this.listen.isSelected());
            zkInfo.setRemark(this.remark.getTextTrim());
            zkInfo.setReadonly(this.readonly.isSelected());
            zkInfo.setSshForward(this.sshForward.isSelected());
            zkInfo.setGroupId(this.group == null ? null : this.group.getGid());
            zkInfo.setCompatibility(this.compatibility.isSelected() ? 1 : null);
            zkInfo.setConnectTimeOut(connectTimeOut.intValue());
            zkInfo.setSessionTimeOut(sessionTimeOut.intValue());
            // 保存数据
            boolean result = ZKConnectJdbcStore.INSTANCE.replace(zkInfo);
            if (result) {
                ZKEventUtil.infoAdded(zkInfo);
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
    protected void bindListeners() {
        // 连接ip处理
        this.hostIp.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为ip端口
            if (t1 != null && t1.contains(":")) {
                try {
                    this.hostIp.setText(t1.split(":")[0]);
                    this.hostPort.setValue(Integer.parseInt(t1.split(":")[1]));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        // ssh配置
        this.sshForward.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                this.sshAuthBox.enable();
                this.sshHostBox.enable();
                this.sshTimeout.enable();
            } else {
                this.sshAuthBox.disable();
                this.sshHostBox.disable();
                this.sshTimeout.disable();
            }
        });
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.group = this.getWindowProp("group");
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.info.add");
    }
}
