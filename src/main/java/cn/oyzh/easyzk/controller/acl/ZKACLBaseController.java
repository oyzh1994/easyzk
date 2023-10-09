package cn.oyzh.easyzk.controller.acl;

import cn.oyzh.fx.plus.controller.FXController;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import lombok.extern.slf4j.Slf4j;


/**
 * zk节点权限基础业务
 *
 * @author oyzh
 * @since 2022/07/07
 */
@Slf4j
public abstract class ZKACLBaseController extends FXController {

    /**
     * 权限
     */
    protected Pane permsNode;

    /**
     * 节点路径
     */
    protected TextField nodePathNode;

    /**
     * zk信息
     */
    protected ZKInfo zkInfo;

    /**
     * zk节点
     */
    protected ZKNode zkNode;

    /**
     * zk树节点
     */
    protected ZKNodeTreeItem zkItem;

    /**
     * zk客户端
     */
    protected ZKClient zkClient;

    /**
     * 获取权限
     *
     * @return 权限内容
     */
    protected String getPerms() {
        CheckBox a = (CheckBox) this.permsNode.getChildren().get(0);
        CheckBox w = (CheckBox) this.permsNode.getChildren().get(1);
        CheckBox r = (CheckBox) this.permsNode.getChildren().get(2);
        CheckBox d = (CheckBox) this.permsNode.getChildren().get(3);
        CheckBox c = (CheckBox) this.permsNode.getChildren().get(4);
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
    public void onViewShown(WindowEvent event) {
        // 获取初始化对象
        this.zkItem = this.getViewProp("zkItem");
        this.zkNode = this.zkItem.value();
        this.zkClient = this.getViewProp("zkClient");
        this.zkInfo = this.zkClient.zkInfo();

        // 寻找节点
        this.permsNode = (Pane) this.view.root().lookup("#perms");
        this.nodePathNode = (TextField) this.view.root().lookup("#nodePath");
        this.nodePathNode.setText(this.zkNode.decodeNodePath());
    }
}
