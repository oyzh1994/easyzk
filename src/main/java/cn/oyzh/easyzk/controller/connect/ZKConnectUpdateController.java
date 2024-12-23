package cn.oyzh.easyzk.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.domain.ZKSSHConfig;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.fx.ZKAuthTableView;
import cn.oyzh.easyzk.fx.ZKFilterTableView;
import cn.oyzh.easyzk.fx.ZKSASLTypeComboBox;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.easyzk.store.ZKConnectStore;
import cn.oyzh.easyzk.store.ZKFilterStore;
import cn.oyzh.easyzk.store.ZKSASLConfigStore;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.vo.ZKAuthVO;
import cn.oyzh.easyzk.vo.ZKFilterVO;
import cn.oyzh.easyzk.zk.ZKSASLUtil;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.FXConst;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupUtil;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.store.jdbc.QueryParam;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;

import java.util.List;

/**
 * zk信息修改业务
 *
 * @author oyzh
 * @since 2020/9/15
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        value = FXConst.FXML_PATH + "connect/zkConnectUpdate.fxml"
)
public class ZKConnectUpdateController extends StageController {

    /**
     * 只读模式
     */
    @FXML
    private FXCheckBox readonly;

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
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * zk信息
     */
    private ZKConnect zkInfo;

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
     * ssh面板
     */
    @FXML
    private FXTab sshTab;

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

    // /**
    //  * ssh连接组件
    //  */
    // @FXML
    // private FlexHBox sshHostBox;
    //
    // /**
    //  * ssh认证组件
    //  */
    // @FXML
    // private FlexHBox sshAuthBox;

    /**
     * ssh面板
     */
    @FXML
    private FXTab saslTab;

    /**
     * 开启sasl
     */
    @FXML
    private FXToggleSwitch saslAuth;

    /**
     * sasl类型
     */
    @FXML
    private ZKSASLTypeComboBox saslType;

    /**
     * sasl用户
     */
    @FXML
    private ClearableTextField saslUser;

    /**
     * sasl密码
     */
    @FXML
    private ClearableTextField saslPassword;

    /**
     * zk连接储存对象
     */
    private final ZKConnectStore connectStore = ZKConnectStore.INSTANCE;

    /**
     * 认证列表
     */
    @FXML
    private ZKAuthTableView authTable;

    /**
     * 认证搜索
     */
    @FXML
    private ClearableTextField authSearchKW;

    /**
     * 过滤列表
     */
    @FXML
    private ZKFilterTableView filterTable;

    /**
     * zk认证配置储存
     */
    private final ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    /**
     * 过滤搜索
     */
    @FXML
    private ClearableTextField filterSearchKW;

    /**
     * zk过滤配置储存
     */
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * sasl配置储存
     */
    private final ZKSASLConfigStore saslConfigStore = ZKSASLConfigStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
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
    private ZKSSHConfig getSSHConfig() {
        ZKSSHConfig sshConfig = new ZKSSHConfig();
        sshConfig.setIid(this.zkInfo.getId());
        sshConfig.setHost(this.sshHost.getText());
        sshConfig.setUser(this.sshUser.getText());
        sshConfig.setPort(this.sshPort.getIntValue());
        sshConfig.setPassword(this.sshPassword.getText());
        sshConfig.setTimeout(this.sshTimeout.getIntValue());
        return sshConfig;
    }

    /**
     * 获取ssh信息
     *
     * @return ssh连接信息
     */
    private ZKSASLConfig getSASLConfig() {
        ZKSASLConfig saslConfig = new ZKSASLConfig();
        saslConfig.setIid(this.zkInfo.getId());
        saslConfig.setUserName(this.saslUser.getText());
        saslConfig.setType(this.saslType.getSelectedItem());
        saslConfig.setPassword(this.saslPassword.getText());
        return saslConfig;
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
            zkInfo.setSaslAuth(this.saslAuth.isSelected());
            zkInfo.setSshForward(this.sshForward.isSelected());
            if (zkInfo.isSSHForward()) {
                zkInfo.setSshConfig(this.getSSHConfig());
            }
            if (zkInfo.isSASLAuth()) {
                zkInfo.setSaslConfig(this.getSASLConfig());
            }
            ZKConnectUtil.testConnect(this.stage, zkInfo);
        }
    }

    /**
     * 修改zk信息
     */
    @FXML
    private void update() {
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
            this.zkInfo.setName(name);
            Number connectTimeOut = this.connectTimeOut.getValue();
            Number sessionTimeOut = this.sessionTimeOut.getValue();

            this.zkInfo.setHost(host.trim());
            // ssh配置
            this.zkInfo.setSshConfig(this.getSSHConfig());
            this.zkInfo.setSshForward(this.sshForward.isSelected());
            // sasl配置
            this.zkInfo.setSaslConfig(this.getSASLConfig());
            this.zkInfo.setSaslAuth(this.saslAuth.isSelected());
            // 移除sasl配置
            ZKSASLUtil.removeSasl(this.zkInfo.getId());
            this.zkInfo.setListen(this.listen.isSelected());
            this.zkInfo.setRemark(this.remark.getTextTrim());
            this.zkInfo.setReadonly(this.readonly.isSelected());
            this.zkInfo.setConnectTimeOut(connectTimeOut.intValue());
            this.zkInfo.setSessionTimeOut(sessionTimeOut.intValue());
            this.zkInfo.setCompatibility(this.compatibility.isSelected() ? 1 : null);
            // 认证列表
            this.zkInfo.setAuths(this.authTable.getAuths());
            // 过滤列表
            this.zkInfo.setFilters(this.filterTable.getFilters());
            // 保存数据
            if (this.connectStore.replace(this.zkInfo)) {
                ZKEventUtil.infoUpdated(this.zkInfo);
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
                NodeGroupUtil.enable(this.sshTab, "ssh");
            } else {
                NodeGroupUtil.disable(this.sshTab, "ssh");
            }
        });
        // sasl配置
        this.saslAuth.selectedChanged((observable, oldValue, newValue) -> {
            if (newValue) {
                NodeGroupUtil.enable(this.saslTab, "sasl");
            } else {
                NodeGroupUtil.disable(this.saslTab, "sasl");
            }
        });
        // 认证监听
        this.authSearchKW.addTextChangeListener((observableValue, s, t1) -> this.initAuthDataList());
        // 过滤监听
        this.filterSearchKW.addTextChangeListener((observableValue, s, t1) -> this.initFilterDataList());
    }

    @Override
    public void onStageShown(@NonNull WindowEvent event) {
        super.onStageShown(event);
        this.zkInfo = this.getWindowProp("zkInfo");
        this.name.setText(this.zkInfo.getName());
        this.remark.setText(this.zkInfo.getRemark());
        this.readonly.setSelected(this.zkInfo.isReadonly());
        this.connectTimeOut.setValue(this.zkInfo.getConnectTimeOut());
        this.sessionTimeOut.setValue(this.zkInfo.getSessionTimeOut());
        this.compatibility.setSelected(this.zkInfo.compatibility34());
        this.hostIp.setText(this.zkInfo.hostIp());
        this.hostPort.setValue(this.zkInfo.hostPort());
        this.listen.setSelected(this.zkInfo.getListen());
        // ssh配置
        this.sshForward.setSelected(this.zkInfo.isSSHForward());
        ZKSSHConfig sshConfig = this.zkInfo.getSshConfig();
        if (sshConfig != null) {
            this.sshHost.setText(sshConfig.getHost());
            this.sshUser.setText(sshConfig.getUser());
            this.sshPort.setValue(sshConfig.getPort());
            this.sshTimeout.setValue(sshConfig.getTimeout());
            this.sshPassword.setText(sshConfig.getPassword());
        }
        // sasl配置
        ZKSASLConfig saslConfig = this.saslConfigStore.getByIid(this.zkInfo.getId());
        this.saslAuth.setSelected(this.zkInfo.isSASLAuth());
        if (saslConfig != null) {
            this.saslType.select(saslConfig.getType());
            this.saslUser.setText(saslConfig.getUserName());
            this.saslPassword.setText(saslConfig.getPassword());
        }
        // 初始化数据
        this.initAuthDataList();
        this.initFilterDataList();
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.info.update");
    }

    /**
     * 初始化过滤
     */
    private void initFilterDataList() {
        if (!this.filterTable.hasData()) {
            List<ZKFilter> list = this.filterStore.selectList(QueryParam.of("iid", this.zkInfo.getId()));
            this.filterTable.setFilters(list);
        } else {
            this.filterTable.setKw(this.filterSearchKW.getText());
        }
    }

    /**
     * 添加过滤
     */
    @FXML
    private void addFilter() {
        ZKFilterVO filter = new ZKFilterVO();
        filter.setEnable(true);
        filter.setPartMatch(true);
        this.filterTable.addFilter(filter);
        this.filterTable.selectLast();
    }

    /**
     * 删除过滤
     */
    @FXML
    private void deleteFilter() {
        ZKFilterVO filter = this.filterTable.getSelectedItem();
        if (filter == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            this.filterTable.removeItem(filter);
        }
    }

    /**
     * 初始化认证
     */
    private void initAuthDataList() {
        if (!this.authTable.hasData()) {
            List<ZKAuth> list = this.authStore.selectList(QueryParam.of("iid", this.zkInfo.getId()));
            this.authTable.setAuths(list);
        } else {
            this.authTable.setKw(this.authSearchKW.getText());
        }
    }

    /**
     * 添加认证
     */
    @FXML
    private void addAuth() {
        ZKAuthVO authVO = new ZKAuthVO();
        authVO.setEnable(true);
        this.authTable.addAuth(authVO);
        this.authTable.selectLast();
    }

    /**
     * 删除认证
     */
    @FXML
    private void deleteAuth() {
        ZKAuthVO authVO = this.authTable.getSelectedItem();
        if (authVO == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            this.authTable.removeItem(authVO);
        }
    }

    /**
     * 复制认证
     */
    @FXML
    private void copyAuth() {
        ZKAuthVO auth = this.authTable.getSelectedItem();
        if (auth == null) {
            return;
        }
        String data = I18nHelper.userName() + " " + auth.getUser() + System.lineSeparator()
                + I18nHelper.password() + " " + auth.getPassword();
        ClipboardUtil.setStringAndTip(data);
    }
}
