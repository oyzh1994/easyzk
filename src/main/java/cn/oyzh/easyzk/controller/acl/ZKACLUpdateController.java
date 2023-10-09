package cn.oyzh.easyzk.controller.acl;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.plus.information.FXAlertUtil;
import cn.oyzh.fx.plus.information.FXToastUtil;
import cn.oyzh.fx.plus.view.FXWindow;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.ZKStyle;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.parser.ZKExceptionParser;
import cn.oyzh.easyzk.util.ZKACLUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;


/**
 * zk节点修改业务
 *
 * @author oyzh
 * @since 2022/12/20
 */
@FXWindow(
        title = "zk节点权限修改",
        iconUrls = ZKConst.ICON_PATH,
        modality = Modality.WINDOW_MODAL,
        cssUrls = ZKStyle.COMMON,
        value = ZKConst.FXML_BASE_PATH + "acl/zkACLUpdate.fxml"
)
@Slf4j
public class ZKACLUpdateController extends ZKACLBaseController {

    /**
     * zk权限信息
     */
    private ZKACL acl;

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
                FXAlertUtil.warn("请最少勾选一项权限！");
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
                FXAlertUtil.warn("修改节点权限失败，未找到对应的权限！");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FXAlertUtil.warn(ex, ZKExceptionParser.INSTANCE);
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
            FXToastUtil.ok("修改节点权限成功！");
            this.closeView();
        } else {
            FXAlertUtil.warn("修改节点权限失败！");
        }
    }

    @Override
    public void onViewShown(WindowEvent event) {
        super.onViewShown(event);
        this.acl = this.getViewProp("acl");

        CheckBox a = (CheckBox) this.permsNode.getChildren().get(0);
        CheckBox w = (CheckBox) this.permsNode.getChildren().get(1);
        CheckBox r = (CheckBox) this.permsNode.getChildren().get(2);
        CheckBox d = (CheckBox) this.permsNode.getChildren().get(3);
        CheckBox c = (CheckBox) this.permsNode.getChildren().get(4);
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
        this.view.hideOnEscape();
    }
}
