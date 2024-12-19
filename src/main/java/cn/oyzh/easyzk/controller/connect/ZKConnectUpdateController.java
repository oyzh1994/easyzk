package cn.oyzh.easyzk.controller.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.controller.filter.ZKFilterAddController;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.easyzk.vo.ZKFilterVO;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.event.ZKFilterAddedEvent;
import cn.oyzh.easyzk.store.ZKConnectJdbcStore;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.text.field.ClearableTextField;
import cn.oyzh.fx.gui.text.field.NumberTextField;
import cn.oyzh.fx.gui.text.field.PortTextField;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.table.FlexTableView;
import cn.oyzh.fx.plus.controls.text.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.toggle.FXToggleSwitch;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import cn.oyzh.fx.plus.window.StageManager;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.store.jdbc.QueryParam;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * zk信息修改业务
 *
 * @author oyzh
 * @since 2020/9/15
 */
@StageAttribute(
        modality = Modality.WINDOW_MODAL,
        iconUrl = ZKConst.ICON_PATH,
        value = ZKConst.FXML_BASE_PATH + "connect/zkConnectUpdate.fxml"
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
     * 备注
     */
    @FXML
    private FlexTextArea remark;

    /**
     * 超时时间
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

    /**
     * zk连接储存对象
     */
    private final ZKConnectJdbcStore connectStore = ZKConnectJdbcStore.INSTANCE;

    /**
     * 搜索词汇
     */
    @FXML
    private ClearableTextField filterSearchKW;

    /**
     * 数据列表
     */
    @FXML
    private FlexTableView<ZKFilterVO> filterTable;

    /**
     * zk过滤配置储存
     */
    private final ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    /**
     * 当前过滤列表
     */
    private List<ZKFilter> filters;

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
    private ZKSSHConnect getSSHInfo() {
        ZKSSHConnect sshConnectInfo = new ZKSSHConnect();
        sshConnectInfo.setHost(this.sshHost.getText());
        sshConnectInfo.setUser(this.sshUser.getText());
        sshConnectInfo.setPort(this.sshPort.getIntValue());
        sshConnectInfo.setPassword(this.sshPassword.getText());
        sshConnectInfo.setTimeout(this.sshTimeout.getIntValue());
        sshConnectInfo.setIid(this.zkInfo.getId());
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
        String name = this.name.getTextTrim();
        this.zkInfo.setName(name);
        Number connectTimeOut = this.connectTimeOut.getValue();
        Number sessionTimeOut = this.sessionTimeOut.getValue();

        this.zkInfo.setHost(host.trim());
        this.zkInfo.setSshConnect(this.getSSHInfo());
        this.zkInfo.setListen(this.listen.isSelected());
        this.zkInfo.setRemark(this.remark.getTextTrim());
        this.zkInfo.setReadonly(this.readonly.isSelected());
        this.zkInfo.setSshForward(this.sshForward.isSelected());
        this.zkInfo.setConnectTimeOut(connectTimeOut.intValue());
        this.zkInfo.setSessionTimeOut(sessionTimeOut.intValue());
        this.zkInfo.setCompatibility(this.compatibility.isSelected() ? 1 : null);
        // 过滤列表
        this.zkInfo.setFilters((List) this.filterTable.getItems());
        // 保存数据
        if (this.connectStore.replace(this.zkInfo)) {
            ZKEventUtil.infoUpdated(this.zkInfo);
            MessageBox.okToast(I18nHelper.operationSuccess());
            this.closeWindow();
        } else {
            MessageBox.warn(I18nHelper.operationFail());
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
        // ssh连接信息
        ZKSSHConnect connectInfo = this.zkInfo.getSshConnect();
        if (connectInfo != null) {
            this.sshHost.setText(connectInfo.getHost());
            this.sshUser.setText(connectInfo.getUser());
            this.sshPort.setValue(connectInfo.getPort());
            this.sshTimeout.setValue(connectInfo.getTimeout());
            this.sshPassword.setText(connectInfo.getPassword());
        }
        this.initFilterDataList();
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }

    @Override
    public String getViewTitle() {
        return I18nResourceBundle.i18nString("base.title.info.update");
    }

    /**
     * 初始化数据列表
     */
    private void initFilterDataList() {
        List<ZKFilter> list = new ArrayList<>();
        if (this.filters == null) {
            this.filters = this.filterStore.selectList(QueryParam.of("iid", this.zkInfo.getId()));
            list = this.filters;
        } else {
            String kw = this.filterSearchKW.getText();
            for (ZKFilter filter : this.filters) {
                if (StringUtil.containsIgnoreCase(filter.getKw(), kw)) {
                    list.add(filter);
                }
            }
        }
        this.filterTable.setItem(ZKFilterVO.convert(list));
    }

    /**
     * 添加过滤
     */
    @FXML
    private void addFilter() {
        StageManager.showStage(ZKFilterAddController.class);
    }

    /**
     * 删除过滤
     */
    @FXML
    private void deleteFilter() {
        ZKFilter filter = this.filterTable.getSelectedItem();
        if (filter == null) {
            return;
        }
        if (MessageBox.confirm(I18nHelper.deleteData())) {
            if (filter.getIid() != null) {
                if (this.filterStore.delete(filter)) {
                    this.filterTable.removeItem(filter);
                } else {
                    MessageBox.warn(I18nHelper.operationFail());
                }
            } else {
                this.filterTable.removeItem(filter);
            }
        }
    }

    /**
     * 过滤新增事件
     */
    @EventSubscribe
    private void filterAdded(ZKFilterAddedEvent event) {
        this.filters.add(event.data());
        this.initFilterDataList();
    }
}
