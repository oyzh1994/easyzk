package cn.oyzh.easyzk.controller.acl;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.easyzk.store.ZKAuthStore2;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.button.CopyButton;
import cn.oyzh.fx.gui.textfield.ClearableTextField;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.FlexFlowPane;
import cn.oyzh.fx.plus.controls.textarea.FlexTextArea;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.button.FlexCheckBox;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.node.NodeMutexes;
import cn.oyzh.fx.plus.util.ClipboardUtil;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.List;


/**
 * zk权限添加业务
 *
 * @author oyzh
 * @since 2022/12/19
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "acl/zkACLAdd.fxml"
)
public class ZKACLAddController extends StageController {

    /**
     * zk树节点
     */
    private ZKNodeTreeItem zkItem;

    /**
     * zk客户端
     */
    private ZKClient zkClient;

    /**
     * 权限
     */
    @FXML
    private FlexFlowPane perms;

    /**
     * 节点路径
     */
    @FXML
    private TextField nodePath;

    /**
     * 权限组件
     */
    @FXML
    private FlexHBox permsBox;

    /**
     * 权限类型
     */
    @FXML
    private FlexComboBox<String> aclType;

    /**
     * digest权限处理1
     */
    @FXML
    private VBox digest1ACL;

    /**
     * digest权限处理2
     */
    @FXML
    private HBox digest2ACL;

    /**
     * digest权限处理3
     */
    @FXML
    private HBox digest3ACL;

    /**
     * 单IP权限处理
     */
    @FXML
    private HBox ip1ACL;

    /**
     * 多IP权限处理
     */
    @FXML
    private HBox ip2ACL;

    /**
     * 摘要信息1，用户名
     */
    @FXML
    private ClearableTextField digestInfo1User;

    /**
     * 摘要信息1，密码明文
     */
    @FXML
    private ClearableTextField digestInfo1Password;

    /**
     * 摘要信息2，user:digest
     */
    @FXML
    private ClearableTextField digestInfo2;

    /**
     * 摘要信息3，已有账密
     */
    @FXML
    private FlexComboBox<ZKAuth> digestInfo3;

    /**
     * 摘要信息
     */
    @FXML
    private Label digestText;

    /**
     * 复制摘要信息
     */
    @FXML
    private CopyButton copyDigestText;

    /**
     * 摘要保存
     */
    @FXML
    private FlexCheckBox digestSaveInfo;

    /**
     * IP单IP内容
     */
    @FXML
    private ClearableTextField ipContent1;

    /**
     * IP多IP内容
     */
    @FXML
    private FlexTextArea ipContent2;

    /**
     * 节点互斥器
     */
    private final NodeMutexes nodeGroupManage = new NodeMutexes();

    /**
     * 认证信息储存
     */
    private final ZKAuthStore2 authStore = ZKAuthStore2.INSTANCE;

    /**
     * 复制摘要信息
     */
    @FXML
    private void copyDigestText() {
        String data = this.digestText.getText();
        ClipboardUtil.setStringAndTip(data, "摘要信息");
    }

    /**
     * 网段模式
     */
    @FXML
    private void segmentModel() {
        String ip = this.ipContent1.getText().trim();
        if (StringUtil.isNotBlank(ip) && !ip.endsWith("/16")) {
            if (!ip.endsWith(".0")) {
                ip += ".0";
            }
            ip = ip.replace("..", ".");
            this.ipContent1.setText(ip + "/16");
        }
    }

    /**
     * 添加权限
     */
    @FXML
    private void addACL() {
        try {
            // 检查类型
            if (!this.aclType.validate()) {
                return;
            }
            // 检查权限
            String perms = this.getPerms();
            if (StringUtil.isBlank(perms)) {
                Node perm = this.perms.getChild(0);
                MessageBox.tipMsg("perms " + I18nHelper.canNotEmpty(), perm);
                return;
            }
            // 执行新增
            if (this.aclType.getSelectedIndex() == 0) {
                this.addWorldACL();
            } else if (this.aclType.getSelectedIndex() == 1) {
                this.addDigestACL1();
            } else if (this.aclType.getSelectedIndex() == 2) {
                this.addDigestACL2();
            } else if (this.aclType.getSelectedIndex() == 3) {
                this.addDigestACL3();
            } else if (this.aclType.getSelectedIndex() == 4) {
                this.addIPACL1();
            } else if (this.aclType.getSelectedIndex() == 5) {
                this.addIPACL2();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 摘要添加zk权限1
     */
    private void addDigestACL1() {
        // 获取内容
        String user = this.digestInfo1User.getText().trim();
        if (StringUtil.isBlank(user)) {
            MessageBox.tipMsg(I18nHelper.userNameCanNotEmpty(), this.digestInfo1User);
            return;
        }
        String password = this.digestInfo1Password.getText().trim();
        if (StringUtil.isBlank(password)) {
            MessageBox.tipMsg(I18nHelper.passwordCanNotEmpty(), this.digestInfo1Password);
            return;
        }
        String digest = ZKAuthUtil.digest(user, password);
        if (digest == null) {
            MessageBox.warn(I18nHelper.operationFail());
            return;
        }
        if (this.zkItem.existDigestACL(digest)) {
            MessageBox.warn("[" + digest + "] " + I18nHelper.alreadyExists());
            return;
        }
        ACL acl = new ACL();
        Id id = new Id("digest", digest);
        acl.setId(id);
        acl.setPerms(ZKACLUtil.toPermInt(this.getPerms()));
        // 新增权限
        if (this.addACL(acl) && this.digestSaveInfo.isSelected()) {
            // 保存认证信息
            this.authStore.replace(new ZKAuth(user, password));
        }
    }

    /**
     * 摘要添加zk权限2
     */
    private void addDigestACL2() {
        // 获取内容
        String digest = this.digestInfo2.getText().trim();
        String[] text = digest.split(":");
        if (text.length != 2) {
            MessageBox.tipMsg(I18nHelper.invalidData(), this.digestInfo2);
            return;
        }
        String user = text[0];
        String password = text[1];
        if (StringUtil.isBlank(user)) {
            MessageBox.tipMsg(I18nHelper.userNameCanNotEmpty(), this.digestInfo2);
            return;
        }
        if (StringUtil.isBlank(password)) {
            MessageBox.tipMsg(I18nHelper.passwordCanNotEmpty(), this.digestInfo2);
            return;
        }
        if (password.length() < 28) {
            MessageBox.tipMsg(I18nHelper.invalidData(), this.digestInfo2);
            return;
        }
        if (this.zkItem.existDigestACL(digest)) {
            MessageBox.warn("[" + digest + "] " + I18nHelper.alreadyExists());
            return;
        }
        ACL acl = new ACL();
        Id id = new Id("digest", digest);
        acl.setId(id);
        acl.setPerms(ZKACLUtil.toPermInt(this.getPerms()));
        // 新增权限
        this.addACL(acl);
    }

    /**
     * 摘要添加zk权限3
     */
    private void addDigestACL3() {
        // 获取内容
        ZKAuth zkAuth = this.digestInfo3.getValue();
        if (zkAuth == null) {
            MessageBox.tipMsg(I18nHelper.contentCanNotEmpty(), this.digestInfo3);
            return;
        }
        String digest = zkAuth.digest();
        if (this.zkItem.existDigestACL(digest)) {
            MessageBox.warn("[" + digest + "] " + I18nHelper.alreadyExists());
            return;
        }
        ACL acl = new ACL();
        Id id = new Id("digest", digest);
        acl.setId(id);
        acl.setPerms(ZKACLUtil.toPermInt(this.getPerms()));
        // 新增权限
        this.addACL(acl);
    }

    /**
     * world添加zk权限
     */
    private void addWorldACL() {
        if (this.zkItem.hasWorldACL()) {
            MessageBox.warn("World " + I18nHelper.permsAlreadyExists());
            return;
        }
        ACL acl = new ACL();
        Id id = new Id("world", "anyone");
        acl.setId(id);
        acl.setPerms(ZKACLUtil.toPermInt(this.getPerms()));
        // 新增权限
        this.addACL(acl);
    }

    /**
     * IP添加zk权限1，单IP
     */
    private void addIPACL1() {
        String perms = this.getPerms();
        String ip = this.ipContent1.getTextTrim();
        ZKACLUtil.checkIP(ip);
        ZKACL acl = new ZKACL();
        acl.setId(new Id("ip", ip));
        acl.setPerms(perms);
        if (this.zkItem.existIPACL(acl.idVal())) {
            MessageBox.warnToast("[" + ip + "] " + I18nHelper.alreadyExists());
        } else {
            this.addACL(acl);
        }
    }

    /**
     * IP添加zk权限2，多IP
     */
    private void addIPACL2() {
        List<ACL> aclList = new ArrayList<>();
        String[] ipList = this.ipContent2.getTextTrim().split(";");
        for (String s : ipList) {
            String[] strArr = s.split(":");
            if (strArr.length < 1) {
                throw new ZKException(s + I18nHelper.invalidData());
            }
            if (strArr.length < 2) {
                throw new ZKException(s + I18nHelper.invalidPerms());
            }
            String ip = strArr[0];
            String perms = strArr[1];
            ZKACLUtil.checkIP(ip);
            ZKACL acl = new ZKACL();
            acl.setId(new Id("ip", ip));
            acl.setPerms(perms);
            if (this.zkItem.existIPACL(acl.idVal())) {
                MessageBox.warnToast("[" + ip + "] " + I18nHelper.alreadyExists());
                return;
            }
            aclList.add(acl);
        }
        this.addACL(aclList);
    }

    /**
     * 新增zk权限
     *
     * @param acl 权限
     * @return 结果
     */
    private boolean addACL(@NonNull ACL acl) {
        return this.addACL(List.of(acl));
    }

    /**
     * 新增zk权限
     *
     * @param list 权限列表
     * @return 结果
     */
    private boolean addACL(@NonNull List<ACL> list) {
        try {
            Stat stat = this.zkClient.addACL(this.zkItem.nodePath(), list);
            if (stat != null) {
                ZKEventUtil.nodeACLAdded(this.zkItem.info());
                this.closeWindow();
                return true;
            }
            MessageBox.warn(I18nHelper.operationFail());
        } catch (Exception ex) {
            MessageBox.exception(ex);
        }
        return false;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 获取初始化对象
        this.zkItem = this.getWindowProp("zkItem");
        this.zkClient = this.getWindowProp("zkClient");
        // 初始化摘要数据
        this.initDigestData();

        // 绑定事件
        this.permsBox.managedProperty().bind(this.permsBox.visibleProperty());

        // 节点互斥
        this.nodeGroupManage.addNodes(this.ip1ACL, this.ip2ACL, this.digest1ACL, this.digest2ACL, this.digest3ACL);
        this.nodeGroupManage.manageBindVisible();

        // 权限类型切换事件
        this.aclType.selectedIndexChanged((observableValue, number, t1) -> {
            // world权限
            if (t1.intValue() == 0) {
                this.permsBox.display();
                this.nodeGroupManage.visible(null);
            } else if (t1.intValue() == 1) {// digest权限1
                this.permsBox.display();
                this.nodeGroupManage.visible(this.digest1ACL);
            } else if (t1.intValue() == 2) {// digest权限2
                this.permsBox.display();
                this.nodeGroupManage.visible(this.digest2ACL);
            } else if (t1.intValue() == 3) {// digest权限3
                this.permsBox.display();
                this.nodeGroupManage.visible(this.digest3ACL);
            } else if (t1.intValue() == 4) {// 单IP权限
                this.permsBox.display();
                this.nodeGroupManage.visible(this.ip1ACL);
            } else if (t1.intValue() == 5) {// 多IP权限
                this.permsBox.disappear();
                this.nodeGroupManage.visible(this.ip2ACL);
            }
        });

        // 如果已有world权限，则默认选中摘要权限
        if (this.zkItem.hasWorldACL()) {
            this.aclType.select(1);
        }

        // 文本监听器
        ChangeListener<String> info1listener = (observableValue, s, t1) -> {
            String user = this.digestInfo1User.getText().trim();
            String password = this.digestInfo1Password.getTextTrim();
            if (StringUtil.isNotBlank(user) && StringUtil.isNotBlank(password)) {
                String digest = ZKAuthUtil.digest(user, password);
                this.digestText.setText(digest);
                this.copyDigestText.display();
            } else {
                this.digestText.setText("");
                this.copyDigestText.disappear();
            }
        };
        // 认证信息更新时，动态显示摘要信息
        this.digestInfo1User.addTextChangeListener((observableValue, s, t1) -> {
            // 内容包含“:”，则直接切割字符为用户名密码
            if (t1 != null && t1.contains(":")) {
                this.digestInfo1User.setText(t1.split(":")[0]);
                this.digestInfo1Password.setText(t1.split(":")[1]);
            }
            info1listener.changed(observableValue, s, t1);
        });
        this.digestInfo1Password.addTextChangeListener(info1listener);
        this.nodePath.setText(this.zkItem.decodeNodePath());
        this.stage.hideOnEscape();
    }

    /**
     * 初始化digest数据
     */
    private void initDigestData() {
        this.digestInfo3.getItems().clear();
        List<ZKAuth> authList = this.authStore.load();
        if (CollectionUtil.isNotEmpty(authList)) {
            this.digestInfo3.addItems(authList);
            this.digestInfo3.setConverter(new SimpleStringConverter<>() {
                @Override
                public String toString(ZKAuth auth) {
                    if (auth == null) {
                        return "";
                    }
                    return auth.getUser() + " :" + auth.getPassword();
                }
            });
            this.digestInfo3.selectFirst();
        }
    }

    /**
     * 获取权限
     *
     * @return 权限内容
     */
    private String getPerms() {
        CheckBox a = (CheckBox) this.perms.getChild(0);
        CheckBox w = (CheckBox) this.perms.getChild(1);
        CheckBox r = (CheckBox) this.perms.getChild(2);
        CheckBox d = (CheckBox) this.perms.getChild(3);
        CheckBox c = (CheckBox) this.perms.getChild(4);
        StringBuilder builder = new StringBuilder();
        if (a.isSelected()) {
            builder.append("a");
        }
        if (w.isSelected()) {
            builder.append("w");
        }
        if (r.isSelected()) {
            builder.append("r");
        }
        if (d.isSelected()) {
            builder.append("d");
        }
        if (c.isSelected()) {
            builder.append("c");
        }
        return builder.toString();
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.addACL();
    }
}
