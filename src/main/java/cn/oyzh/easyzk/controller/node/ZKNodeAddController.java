package cn.oyzh.easyzk.controller.node;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.plus.controller.StageController;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import cn.oyzh.fx.plus.controls.box.FlexHBox;
import cn.oyzh.fx.plus.controls.box.FlexVBox;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.controls.tab.FXTab;
import cn.oyzh.fx.plus.controls.text.FXLabel;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.window.StageAttribute;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;

import java.util.List;


/**
 * zk子节点添加业务
 *
 * @author oyzh
 * @since 2020/10/09
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "node/zkNodeAdd.fxml"
)
public class ZKNodeAddController extends StageController {

    /**
     * 权限tab
     */
    @FXML
    private FXTab aclTab;

    /**
     * 数据tab
     */
    @FXML
    private FXTab dataTab;

    /**
     * 节点路径
     */
    @FXML
    private ClearableTextField nodePath;

    /**
     * 节点数据
     */
    @FXML
    private FlexTextArea nodeData;

    /**
     * 节点路径预览组件
     */
    @FXML
    private HBox nodePathPreviewBox;

    /**
     * 节点路径预览
     */
    @FXML
    private TextField nodePathPreview;

    /**
     * 父节点值组件
     */
    @FXML
    private HBox parentNodeBox;

    /**
     * 父节点值
     */
    @FXML
    private TextField parentNode;

    /**
     * 创建模式下拉选择框
     */
    @FXML
    private FlexComboBox<String> createMode;

    /**
     * zk客户端
     */
    private ZKClient zkClient;

    /**
     * 节点路径数据
     */
    private String nodePathText;

    /**
     * 权限类型
     */
    @FXML
    private FlexComboBox<String> aclType;

    /**
     * 权限组件
     */
    @FXML
    private FlexHBox perms;

    /**
     * ip权限
     */
    @FXML
    private FlexHBox ipACL;

    /**
     * 摘要权限
     */
    @FXML
    private FlexVBox digestACL;

    /**
     * 摘要权限内容
     */
    @FXML
    private FXLabel digestText;

    /**
     * ip权限内容
     */
    @FXML
    private ClearableTextField ipContent;

    /**
     * 摘要权限用户名
     */
    @FXML
    private ClearableTextField digestUser;

    /**
     * 摘要权限密码
     */
    @FXML
    private ClearableTextField digestPassword;

    /**
     * 添加zk节点
     */
    @FXML
    private void addNode() {
        if (!this.nodePath.validate()) {
            this.dataTab.selectTab();
            return;
        }
        // 获取权限
        ACL acl = this.getACL();
        if (acl == null) {
            this.aclTab.selectTab();
            return;
        }
        // 获取节点值
        String nodeData = this.nodeData.getText();
        try {
            // 获取创建模式
            CreateMode createMode = CreateMode.fromFlag(this.createMode.getSelectedIndex());
            // 新增节点
            String node = this.zkClient.create(this.nodePathText, nodeData.getBytes(), List.of(acl), null, createMode, true);
            if (node == null) {
                MessageBox.warnToast(I18nHelper.operationFail());
            } else {
                ZKEventUtil.nodeAdded(this.zkClient.zkInfo(), this.nodePathText);
                this.closeWindow();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 粘贴数据
     */
    @FXML
    private void pasteData() {
        this.nodeData.paste();
        this.nodeData.requestFocus();
    }

    /**
     * 清空数据
     */
    @FXML
    private void clearData() {
        this.nodeData.clear();
        this.nodeData.requestFocus();
    }

    /**
     * 解析为json
     */
    @FXML
    private void parseToJson() {
        String text = this.nodeData.getTextTrim();
        try {
            if ("json".equals(this.nodeData.getUserData())) {
                String jsonStr = JSONUtil.toJsonStr(text);
                this.nodeData.setText(jsonStr);
                this.nodeData.setUserData("text");
            } else if (text.contains("{") || text.contains("[") || "text".equals(this.nodeData.getUserData())) {
                String jsonStr = JSONUtil.toJsonPrettyStr(text);
                this.nodeData.setText(jsonStr);
                this.nodeData.setUserData("json");
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * 获取权限
     *
     * @return 权限
     */
    private ACL getACL() {
        StringBuilder builder = new StringBuilder();
        CheckBox a = (CheckBox) this.perms.getChildren().get(0);
        CheckBox w = (CheckBox) this.perms.getChildren().get(1);
        CheckBox r = (CheckBox) this.perms.getChildren().get(2);
        CheckBox d = (CheckBox) this.perms.getChildren().get(3);
        CheckBox c = (CheckBox) this.perms.getChildren().get(4);
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

        if (builder.isEmpty()) {
            this.perms.requestFocus();
            MessageBox.warn(I18nHelper.invalidData());
            return null;
        }

        // 获取权限
        int perms = ZKACLUtil.toPermInt(builder.toString());

        // 开放权限
        if (this.aclType.getSelectedIndex() == 0) {
            return new ACL(perms, new Id("world", "anyone"));
        }

        // 摘要权限
        if (this.aclType.getSelectedIndex() == 1) {
            // 获取内容
            String user = this.digestUser.getText().trim();
            if (StringUtil.isBlank(user)) {
                MessageBox.tipMsg(I18nHelper.userNameCanNotEmpty(), this.digestUser);
                return null;
            }
            String password = this.digestPassword.getText().trim();
            if (StringUtil.isBlank(password)) {
                MessageBox.tipMsg(I18nHelper.passwordCanNotEmpty(), this.digestPassword);
                return null;
            }
            String digest = ZKAuthUtil.digest(user, password);
            return new ACL(perms, new Id("digest", digest));
        }

        // ip权限
        if (this.aclType.getSelectedIndex() == 2) {
            // 获取内容
            String ip = this.ipContent.getTextTrim();
            try {
                ZKACLUtil.checkIP(ip);
            } catch (Exception ex) {
                MessageBox.tipMsg(ex.getMessage(), this.digestPassword);
                return null;
            }
            return new ACL(perms, new Id("ip", ip));
        }
        return null;
    }

    @Override
    protected void bindListeners() {
        // 节点路径变化处理
        this.nodePath.addTextChangeListener((observableValue, s, t1) -> {
            if (StringUtil.isNotBlank(t1)) {
                this.nodePathText = ZKNodeUtil.concatPath(this.parentNode.getText(), t1).trim();
                this.nodePathPreviewBox.setVisible(true);
            } else {
                this.nodePathText = "";
                this.nodePathPreviewBox.setVisible(false);
            }
            this.nodePathPreview.setText(this.nodePathText);
        });

        // 权限变化处理
        this.aclType.selectedIndexChanged((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                this.ipACL.disappear();
                this.digestACL.disappear();
            } else if (newValue.intValue() == 1) {
                this.ipACL.disappear();
                this.digestACL.display();
            } else if (newValue.intValue() == 2) {
                this.ipACL.display();
                this.digestACL.disappear();
            }
        });

        // 文本监听器
        ChangeListener<String> info1listener = (observableValue, s, t1) -> {
            String user = this.digestUser.getText().trim();
            String password = this.digestPassword.getTextTrim();
            if (StringUtil.isNotBlank(user) && StringUtil.isNotBlank(password)) {
                String digest = ZKAuthUtil.digest(user, password);
                this.digestText.setText(digest);
            } else {
                this.digestText.setText("");
            }
        };

        // 认证信息更新时，动态显示摘要信息
        this.digestUser.addTextChangeListener(info1listener);
        this.digestPassword.addTextChangeListener(info1listener);
    }

    @Override
    public void onStageShown(WindowEvent event) {
        this.parentNodeBox.managedProperty().bind(this.parentNode.visibleProperty());
        ZKNodeTreeItem zkItem = this.getWindowProp("zkItem");
        this.zkClient = this.getWindowProp("zkClient");
        if (zkItem != null) {
            this.parentNode.setVisible(true);
            this.parentNode.setText(zkItem.nodePath());
        } else {
            this.parentNode.setVisible(false);
        }
        this.nodePath.requestFocus();
        this.nodePathPreviewBox.managedProperty().bind(this.nodePathPreviewBox.visibleProperty());
        this.stage.switchOnTab();
        this.stage.hideOnEscape();
        super.onStageShown(event);
    }

    @Override
    public String getViewTitle() {
        return I18nHelper.addNode();
    }
}
