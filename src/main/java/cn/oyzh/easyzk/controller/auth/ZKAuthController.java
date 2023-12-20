package cn.oyzh.easyzk.controller.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.event.msg.ZKAuthMsg;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexVBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.event.EventUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeGroupManage;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

import java.util.List;


/**
 * zk节点认证业务
 *
 * @author oyzh
 * @since 2022/06/07
 */
//@Slf4j
@StageAttribute(
        title = "zk节点认证",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        // cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "auth/zkAuth.fxml"
)
public class ZKAuthController extends Controller {

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
     * 节点路径
     */
    @FXML
    private TextField nodePath;

    /**
     * 保存信息1
     */
    @FXML
    private FlexCheckBox saveInfo1;

    /**
     * zk节点
     */
    private ZKNode zkNode;

    /**
     * zk树节点
     */
    private ZKNodeTreeItem zkItem;

    /**
     * 认证方式
     */
    @FXML
    private FlexComboBox<String> authType;

    /**
     * 认证方式1
     */
    @FXML
    private FlexVBox authType1;

    /**
     * 认证方式2
     */
    @FXML
    private FlexVBox authType2;

    /**
     * 认证信息列表
     */
    @FXML
    private FlexComboBox<ZKAuth> authList;

    /**
     * 认证储存
     */
    private final ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    /**
     * 节点互斥器
     */
    private final NodeGroupManage nodeGroupManage = new NodeGroupManage();

    /**
     * 认证
     */
    @FXML
    protected void auth() {
        try {
            String user = null, password = null;
            if (this.authType1.isVisible()) {
                user = this.user.getText().trim();
                password = this.password.getText().trim();
                if (StrUtil.isBlank(user)) {
                    MessageBox.tipMsg("用户名不能为空！", this.user);
                    return;
                }
                if (StrUtil.isBlank(password)) {
                    MessageBox.tipMsg("密码不能为空！", this.password);
                    return;
                }
            } else if (this.authType2.isVisible()) {
                // 获取内容
                ZKAuth zkAuth = this.authList.getValue();
                if (zkAuth == null) {
                    MessageBox.tipMsg("未选择数据或无数据！", this.authList);
                    return;
                }
                user = zkAuth.getUser();
                password = zkAuth.getPassword();
            }
            if (user != null && password != null) {
                this.auth(user, password);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 认证节点
     *
     * @param user     用户名
     * @param password 密码
     */
    private void auth(String user, String password) {
        try {
            ZKClient zkClient = this.zkItem.client();
            int result = ZKAuthUtil.authNode(user, password, zkClient, this.zkNode);
            ZKAuthMsg authMsg = new ZKAuthMsg();
            authMsg.user(user).password(password).item(this.zkItem);
            if (result == 1) {
                if (this.saveInfo1.isSelected()) {
                    this.authStore.add(new ZKAuth(user, password));
                }
                authMsg.result(true);
                EventUtil.fire(ZKEventTypes.ZK_AUTH_SUCCESS, authMsg);
                MessageBox.okToast("认证成功！");
                this.closeStage();
            } else if (this.zkNode.aclEmpty() || this.zkNode.hasDigestACL()) {
                EventUtil.fire(ZKEventTypes.ZK_AUTH_FAIL, authMsg);
                MessageBox.warn("认证失败！");
            } else {
                EventUtil.fire(ZKEventTypes.ZK_AUTH_FAIL, authMsg);
                MessageBox.warn("认证失败或此节点无需认证！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex, "认证失败");
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.zkItem = this.getStageProp("zkItem");
        this.zkNode = this.zkItem.value();
        this.nodePath.setText(this.zkNode.decodeNodePath());
        this.nodeGroupManage.addNodes(this.authType1, this.authType2);
        this.nodeGroupManage.manageBindVisible();
        this.authType.selectedIndexChanged((o, toggle, t1) -> {
            if (t1.intValue() == 0) {
                this.nodeGroupManage.visible(this.authType1);
            } else if (t1.intValue() == 1) {
                this.nodeGroupManage.visible(this.authType2);
            }
        });
        this.authList.getItems().clear();
        ZKClient client = this.zkItem.client();
        List<ZKAuth> authList = this.authStore.load();
        if (CollUtil.isNotEmpty(authList)) {
            this.authList.addItems(authList);
            this.authList.setConverter(new SimpleStringConverter<>() {
                @Override
                public String toString(ZKAuth auth) {
                    String text = "";
                    if (auth != null) {
                        text = auth.getUser() + ":" + auth.getPassword();
                        if (ZKAuthUtil.isAuthed(client, auth)) {
                            text += " (已认证)";
                        }
                    }
                    return text;
                }
            });
            this.authList.selectFirst();
        }
        // 设置一个摘要用户名
        if (this.zkNode.getDigestACLs().size() == 1) {
            this.user.setText(this.zkNode.getDigestACLs().get(0).digestUser());
        } else if (this.zkNode.aclEmpty() && !authList.isEmpty()) {// 选中摘要列表认证
            this.authType.select(1);
        }
        this.stage.switchOnTab();
        this.stage.hideOnEscape();

        this.user.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为用户名密码
            if (t1 != null && t1.contains(":")){
                this.user.setText(t1.split(":")[0]);
                this.password.setText(t1.split(":")[1]);
            }
        });
    }
}
