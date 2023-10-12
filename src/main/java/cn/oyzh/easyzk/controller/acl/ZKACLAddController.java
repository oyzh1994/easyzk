package cn.oyzh.easyzk.controller.acl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import cn.oyzh.easyzk.parser.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.SimpleStringConverter;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.controls.FlexCheckBox;
import cn.oyzh.fx.plus.controls.FlexComboBox;
import cn.oyzh.fx.plus.controls.FlexHBox;
import cn.oyzh.fx.plus.controls.FlexTextArea;
import cn.oyzh.fx.plus.ext.ClearableTextField;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.information.FXTipUtil;
import cn.oyzh.fx.plus.information.FXToastUtil;
import cn.oyzh.fx.plus.node.NodeGroupManage;
import cn.oyzh.fx.plus.stage.StageAttribute;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@StageAttribute(
        title = "zk节点权限新增",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "acl/zkACLAdd.fxml"
)
public class ZKACLAddController extends Controller {


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
    private Pane perms;

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
    private SVGGlyph copyDigestText;

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
    private final NodeGroupManage nodeGroupManage = new NodeGroupManage();

    /**
     * 认证信息储存
     */
    private final ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    /**
     * 复制摘要信息
     */
    @FXML
    private void copyDigestText() {
        String data = this.digestText.getText();
        if (FXUtil.clipboardCopy(data)) {
            FXToastUtil.ok("已复制摘要信息到剪贴板");
        } else {
            FXAlertUtil.warn("复制摘要信息到剪贴板失败！");
        }
    }

    /**
     * 网段模式
     */
    @FXML
    private void segmentModel() {
        String ip = this.ipContent1.getText().trim();
        if (StrUtil.isNotBlank(ip) && !ip.endsWith("/16")) {
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
            if (!this.aclType.validate()) {
                return;
            }
            String perms = this.getPerms();
            if (StrUtil.isBlank(perms)) {
                FXAlertUtil.warn("请最少勾选一项权限！");
                return;
            }
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
            FXAlertUtil.warn(ex, ZKExceptionParser.INSTANCE);
        }
    }

    /**
     * 摘要添加zk权限1
     */
    private void addDigestACL1() {
        // 获取内容
        String user = this.digestInfo1User.getText().trim();
        if (StrUtil.isBlank(user)) {
            FXTipUtil.tip("用户名不能为空！", this.digestInfo1User);
            return;
        }
        String password = this.digestInfo1Password.getText().trim();
        if (StrUtil.isBlank(password)) {
            FXTipUtil.tip("密码不能为空！", this.digestInfo1Password);
            return;
        }
        String digest = ZKAuthUtil.digest(user, password);
        if (digest == null) {
            FXAlertUtil.warn("认证信息处理异常！");
            return;
        }
        if (this.zkItem.existDigestACL(digest)) {
            FXAlertUtil.warn("此摘要认证信息(Digest)已存在！");
            return;
        }
        ACL acl = new ACL();
        Id id = new Id("digest", digest);
        acl.setId(id);
        acl.setPerms(ZKACLUtil.toPermInt(this.getPerms()));
        // 新增权限
        if (this.addACL(acl) && this.digestSaveInfo.isSelected()) {
            // 保存认证信息
            this.authStore.add(new ZKAuth(user, password));
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
            FXTipUtil.tip("数据格式异常！", this.digestInfo2);
            return;
        }
        String user = text[0];
        String password = text[1];
        if (StrUtil.isBlank(user)) {
            FXTipUtil.tip("用户名不能为空！", this.digestInfo2);
            return;
        }
        if (StrUtil.isBlank(password)) {
            FXTipUtil.tip("密码摘要不能为空！", this.digestInfo2);
            return;
        }
        if (password.length() < 28) {
            FXTipUtil.tip("密码摘要格式异常！", this.digestInfo2);
            return;
        }
        if (this.zkItem.existDigestACL(digest)) {
            FXAlertUtil.warn("此摘要认证信息(Digest)已存在！");
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
            FXTipUtil.tip("未选择数据或无数据！", this.digestInfo3);
            return;
        }
        String digest = zkAuth.digest();
        if (this.zkItem.existDigestACL(digest)) {
            FXAlertUtil.warn("此摘要认证信息(Digest)已存在！");
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
            FXAlertUtil.warn("World(开放认证)权限已存在！");
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
            FXToastUtil.warn("IP" + ip + "已存在！");
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
                throw new ZKException(s + "无效的数据");
            }
            if (strArr.length < 2) {
                throw new ZKException(s + "权限无效");
            }
            String ip = strArr[0];
            String perms = strArr[1];
            ZKACLUtil.checkIP(ip);
            ZKACL acl = new ZKACL();
            acl.setId(new Id("ip", ip));
            acl.setPerms(perms);
            if (this.zkItem.existIPACL(acl.idVal())) {
                FXToastUtil.warn("IP" + ip + "已存在！");
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
                this.zkItem.refreshACL();
                FXToastUtil.ok("添加权限成功！");
                this.closeStage();
                return true;
            }
            FXAlertUtil.warn("添加权限失败！");
        } catch (Exception e) {
            FXAlertUtil.warn(e, ZKExceptionParser.INSTANCE);
        }
        return false;
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        // 获取初始化对象
        this.zkItem = this.getStageProp("zkItem");
        this.zkClient = this.getStageProp("zkClient");
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
                this.permsBox.showNode();
                this.nodeGroupManage.visible(null);
            } else if (t1.intValue() == 1) {// digest权限1
                this.permsBox.showNode();
                this.nodeGroupManage.visible(this.digest1ACL);
            } else if (t1.intValue() == 2) {// digest权限2
                this.permsBox.showNode();
                this.nodeGroupManage.visible(this.digest2ACL);
            } else if (t1.intValue() == 3) {// digest权限3
                this.permsBox.showNode();
                this.nodeGroupManage.visible(this.digest3ACL);
            } else if (t1.intValue() == 4) {// 单IP权限
                this.permsBox.showNode();
                this.nodeGroupManage.visible(this.ip1ACL);
            } else if (t1.intValue() == 5) {// 多IP权限
                this.permsBox.hideNode();
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
            if (StrUtil.isNotBlank(user) && StrUtil.isNotBlank(password)) {
                String digest = ZKAuthUtil.digest(user, password);
                this.digestText.setText(digest);
                this.copyDigestText.showNode();
            } else {
                this.digestText.setText("");
                this.copyDigestText.hideNode();
            }
        };

        // 认证信息更新时，动态显示摘要信息
        this.digestInfo1User.addTextChangedListener(info1listener);
        this.digestInfo1Password.addTextChangedListener(info1listener);
        this.nodePath.setText(this.zkItem.decodeNodePath());
        this.stage.hideOnEscape();
    }

    /**
     * 初始化digest数据
     */
    private void initDigestData() {
        this.digestInfo3.getItems().clear();
        List<ZKAuth> authList = this.authStore.load();
        if (CollUtil.isNotEmpty(authList)) {
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
        CheckBox a = (CheckBox) this.perms.getChildren().get(0);
        CheckBox w = (CheckBox) this.perms.getChildren().get(1);
        CheckBox r = (CheckBox) this.perms.getChildren().get(2);
        CheckBox d = (CheckBox) this.perms.getChildren().get(3);
        CheckBox c = (CheckBox) this.perms.getChildren().get(4);
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
}
