package cn.oyzh.easyzk.controller.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.tab.FlexTabPane;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.controls.textfield.NumberTextField;
import cn.oyzh.fx.plus.controls.textfield.PortTextField;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;

/**
 * zk信息修改业务
 *
 * @author oyzh
 * @since 2020/9/15
 */
//@Slf4j
@StageAttribute(
        title = "zk信息修改",
        modality = Modality.WINDOW_MODAL,
        iconUrls = ZKConst.ICON_PATH,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "info/zkInfoUpdate.fxml"
)
public class ZKInfoUpdateController extends Controller {

    /**
     * 只读模式
     */
    @FXML
    private FlexCheckBox readonly;

    /**
     * 监听节点
     */
    @FXML
    private FlexCheckBox listen;

    /**
     * 集群模式
     */
    @FXML
    private FlexCheckBox cluster;

    /**
     * 兼容模式开关
     */
    @FXML
    private FlexCheckBox compatibility;

    /**
     * tab组件
     */
    @FXML
    private FlexTabPane tabPane;

    /**
     * zk信息
     */
    private ZKInfo zkInfo;

    /**
     * 名称
     */
    @FXML
    private ClearableTextField name;

    /**
     * 连接地址
     */
    @FXML
    private FlexTextArea host;

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
     * 单节点组件
     */
    @FXML
    private FlexHBox hostBox1;

    /**
     * 集群组件
     */
    @FXML
    private FlexHBox hostBox2;

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
     * zk连接储存对象
     */
    private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    /**
     * 获取连接地址
     *
     * @return 连接地址
     */
    private String getHost() {
        String hostText;
        String host = this.host.getTextTrim();
        String hostIp = this.hostIp.getTextTrim();
        this.tabPane.select(0);
        if (this.cluster.isSelected()) {
            if (host.contains("：")) {
                this.tabPane.select(0);
                MessageBox.tipMsg("集群地址不合法！", this.host);
                return null;
            }
            hostText = host;
        } else {
            if (!this.hostPort.validate()) {
                this.tabPane.select(0);
                return null;
            }
            if (!this.hostIp.validate()) {
                this.tabPane.select(0);
                return null;
            }
            hostText = hostIp + ":" + this.hostPort.getValue();
        }
        return hostText;
    }

    /**
     * 测试连接
     */
    @FXML
    private void testConnect() {
        // 检查连接地址
        String host = this.getHost();
        if (StrUtil.isBlank(host) || StrUtil.isBlank(host.split(":")[0])) {
            MessageBox.warn("请填写地址");
        } else {
            ZKConnectUtil.testConnect(this.stage, host, 5);
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
        if (this.cluster.isSelected()) {
            // 校验名称是否未填
            if (!this.name.validate()) {
                this.tabPane.select(0);
                return;
            }
        } else {
            // 名称未填，则直接以host为名称
            if (StrUtil.isBlank(this.name.getTextTrim())) {
                this.name.setText(host.replace(":", "_"));
            }
        }
        String name = this.name.getTextTrim();
        this.zkInfo.setName(name);
        Number connectTimeOut = this.connectTimeOut.getValue();
        Number sessionTimeOut = this.sessionTimeOut.getValue();

        this.zkInfo.setRemark(this.remark.getTextTrim());
        this.zkInfo.setHost(host.trim());
        this.zkInfo.setListen(this.listen.isSelected());
        this.zkInfo.setCluster(this.cluster.isSelected());
        this.zkInfo.setReadonly(this.readonly.isSelected());
        this.zkInfo.setConnectTimeOut(connectTimeOut.intValue());
        this.zkInfo.setSessionTimeOut(sessionTimeOut.intValue());
        this.zkInfo.setCompatibility(this.compatibility.isSelected() ? 1 : null);
        // 保存数据
        if (this.infoStore.update(this.zkInfo)) {
            ZKEventUtil.infoUpdated(this.zkInfo);
            MessageBox.okToast("修改ZK信息成功!");
            this.closeStage();
        } else {
            MessageBox.warn("修改失败！");
        }
    }

    @Override
    public void onStageShown(@NonNull WindowEvent event) {
        this.zkInfo = this.getStageProp("zkInfo");
        this.name.setText(this.zkInfo.getName());
        this.remark.setText(this.zkInfo.getRemark());
        this.readonly.setSelected(this.zkInfo.isReadonly());
        this.connectTimeOut.setValue(this.zkInfo.getConnectTimeOut());
        this.sessionTimeOut.setValue(this.zkInfo.getSessionTimeOut());
        this.compatibility.setSelected(this.zkInfo.compatibility34());

        this.hostBox1.managedBindVisible();
        this.hostBox2.managedBindVisible();
        this.host.setText(this.zkInfo.getHost());
        this.hostIp.setText(this.zkInfo.hostIp());
        this.hostPort.setValue(this.zkInfo.hostPort());
        this.cluster.selectedChanged((obs, o, n) -> {
            this.hostBox1.setVisible(!n);
            this.hostBox2.setVisible(n);
        });
        this.listen.setSelected(this.zkInfo.getListen());
        this.cluster.setSelected(this.zkInfo.isCluster());
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
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
    }
}
