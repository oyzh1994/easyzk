package cn.oyzh.easyzk.controller.acl;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKACLUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controller.Controller;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;


/**
 * zk节点修改业务
 *
 * @author oyzh
 * @since 2022/12/20
 */
@StageAttribute(
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        value = ZKConst.FXML_BASE_PATH + "acl/zkACLUpdate.fxml"
)
public class ZKACLUpdateController extends Controller {

    /**
     * zk权限信息
     */
    private ZKACL acl;

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
     * 权限类型
     */
    @FXML
    private TextField aclType;

    /**
     * 摘要权限
     */
    @FXML
    private TextField digest;

    /**
     * ip权限
     */
    @FXML
    private TextField ip;

    /**
     * ip权限控件
     */
    @FXML
    private HBox ipACL;

    /**
     * 摘要权限控件
     */
    @FXML
    private HBox digestACL;

    /**
     * 修改zk权限
     */
    @FXML
    private void updateACL() {
        try {
            String perms = this.getPerms();
            if (StrUtil.isBlank(perms)) {
                MessageBox.warn(I18nResourceBundle.i18nString("base.contentNotEmpty"));
                return;
            }
            List<ACL> aclList = this.zkClient.getACL(this.zkItem.nodePath());
            // 权限id
            String idVal = this.acl.isWorldACL() ? "world" : this.acl.schemeVal();
            boolean updateFlag = false;
            for (ACL acl : aclList) {
                if (acl.getId().getScheme().equals(idVal)) {
                    acl.setPerms(ZKACLUtil.toPermInt(perms));
                    updateFlag = true;
                }
            }
            if (updateFlag) {
                this.updateACL(aclList);
            } else {
                MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            MessageBox.exception(ex);
        }
    }

    /**
     * 修改zk权限
     *
     * @param aclList 权限列表
     */
    private void updateACL(@NonNull List<ACL> aclList) throws Exception {
        Stat stat = this.zkClient.setACL(this.zkItem.nodePath(), aclList);
        if (stat != null) {
            this.zkItem.refreshACL();
            MessageBox.okToast(I18nResourceBundle.i18nString("base.actionSuccess"));
            this.closeStage();
        } else {
            MessageBox.warn(I18nResourceBundle.i18nString("base.actionFail"));
        }
    }

    @Override
    public void onStageShown(WindowEvent event) {
        super.onStageShown(event);
        this.acl = this.getStageProp("acl");
        // 获取初始化对象
        this.zkItem = this.getStageProp("zkItem");
        this.zkClient = this.getStageProp("zkClient");

        CheckBox a = (CheckBox) this.perms.getChildren().get(0);
        CheckBox w = (CheckBox) this.perms.getChildren().get(1);
        CheckBox r = (CheckBox) this.perms.getChildren().get(2);
        CheckBox d = (CheckBox) this.perms.getChildren().get(3);
        CheckBox c = (CheckBox) this.perms.getChildren().get(4);
        a.setSelected(this.acl.hasAdminPerm());
        w.setSelected(this.acl.hasWritePerm());
        r.setSelected(this.acl.hasReadPerm());
        d.setSelected(this.acl.hasDeletePerm());
        c.setSelected(this.acl.hasCreatePerm());

        // ip权限相关
        if (this.acl.isIPACL()) {
            this.ipACL.setManaged(true);
            this.ipACL.setVisible(true);
            this.ip.setText(this.acl.idVal());
        } else if (this.acl.isDigestACL()) {// 摘要权限相关
            this.digestACL.setManaged(true);
            this.digestACL.setVisible(true);
            this.digest.setText(this.acl.idVal());
        }
        this.aclType.setText(this.acl.schemeFriend().friendlyValue().toString()
                + "(" + this.acl.schemeFriend().value().toString().toUpperCase() + ")");
        this.nodePath.setText(this.zkItem.decodeNodePath());
        this.stage.hideOnEscape();
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

    @Override
    public String i18nId() {
        return "acl.update";
    }
}
