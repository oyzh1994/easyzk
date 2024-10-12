package cn.oyzh.easyzk.trees.data;

import cn.oyzh.easyzk.controller.auth.ZKAuthAuthController;
import cn.oyzh.easyzk.controller.node.ZKNodeAddController;
import cn.oyzh.easyzk.controller.node.ZKNodeExportController;
import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeView;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.log.JulLog;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.common.thread.TaskBuilder;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.trees.RichTreeView;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.ACL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKDataTreeItem extends ZKTreeItem<ZKDataTreeItemValue> {

    public ZKDataTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKDataTreeItemValue());
    }

    /**
     * 当前节点的父zk节点
     *
     * @return 父zk节点
     */
    public ZKConnectTreeItem parent() {
        TreeItem parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    @Override
    public void onPrimaryDoubleClick() {
        ZKEventUtil.connectionOpened(this.parent());
    }
}
