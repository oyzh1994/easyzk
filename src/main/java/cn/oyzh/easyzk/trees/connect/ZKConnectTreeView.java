package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyzk.controller.connect.ZKConnectAddController;
import cn.oyzh.easyzk.event.connect.ZKAddConnectEvent;
import cn.oyzh.easyzk.event.group.ZKAddGroupEvent;
import cn.oyzh.easyzk.event.ZKInfoAddedEvent;
import cn.oyzh.easyzk.event.ZKInfoUpdatedEvent;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tree.view.RichTreeCell;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

/**
 * zk树
 *
 * @author oyzh
 * @since 2023/1/29
 */
public class ZKConnectTreeView extends RichTreeView implements FXEventListener {

    @Override
    protected void initTreeView() {
        this.dragContent = "zk_connect_tree_drag";
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell<>());
        super.initTreeView();
    }

    @Override
    protected void initRoot() {
        super.setRoot(new ZKRootTreeItem(this));
        this.getRoot().expend();
        super.initRoot();
    }

    @Override
    protected void initEvenListener() {
        super.initEvenListener();
        // 暂停按键处理
        KeyListener.listenReleased(this, KeyCode.PAUSE, event -> {
            TreeItem<?> item = this.getSelectedItem();
            if (item instanceof ZKConnectTreeItem treeItem) {
                treeItem.closeConnect();
            }
        });
    }

    @Override
    public ZKRootTreeItem getRoot() {
        return (ZKRootTreeItem) super.getRoot();
    }

    /**
     * 关闭连接
     */
    public void closeConnects() {
        for (ZKConnectTreeItem treeItem : this.getRoot().getConnectedItems()) {
            ThreadUtil.startVirtual(() -> treeItem.closeConnect(false));
        }
    }

    @Override
    public void expand() {
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof ZKConnectTreeItem treeItem) {
            treeItem.expend();
        } else if (item instanceof RichTreeItem<?> treeItem) {
            treeItem.expend();
        }
        if (item != null) {
            this.select(item);
        }
    }

    @Override
    public void collapse() {
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof ZKConnectTreeItem treeItem) {
            treeItem.collapse();
        } else if (item instanceof RichTreeItem<?> treeItem) {
            treeItem.collapse();
        }
        if (item != null) {
            this.select(item);
        }
    }

    /**
     * 添加分组
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addGroup(ZKAddGroupEvent event) {
        this.getRoot().addGroup();
    }

    /**
     * 连接新增事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoAdded(ZKInfoAddedEvent event) {
        this.getRoot().infoAdded(event.data());
    }

    /**
     * 连接变更事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void infoUpdated(ZKInfoUpdatedEvent event) {
        this.getRoot().infoUpdated(event.data());
    }

    /**
     * 添加连接
     */
    @EventSubscribe
    private void addConnect(ZKAddConnectEvent event) {
        StageManager.showStage(ZKConnectAddController.class, this.window());
    }
}
