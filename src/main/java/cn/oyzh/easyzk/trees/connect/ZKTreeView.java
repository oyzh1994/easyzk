package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoAddController;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKAddConnectEvent;
import cn.oyzh.easyzk.event.ZKAddGroupEvent;
import cn.oyzh.easyzk.event.ZKInfoAddedEvent;
import cn.oyzh.easyzk.event.ZKInfoUpdatedEvent;
import cn.oyzh.easyzk.store.ZKSettingJdbcStore;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.treeView.RichTreeCell;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * zk树
 *
 * @author oyzh
 * @since 2023/1/29
 */
@Accessors(chain = true, fluent = true)
public class ZKTreeView extends RichTreeView implements FXEventListener {

    /**
     * 搜索中标志位
     */
    @Getter
    private volatile boolean searching;

    /**
     * 配置储存对象
     */
    private final ZKSetting setting = ZKSettingJdbcStore.SETTING;

    public ZKTreeView() {
        this.dragContent = "zk_tree_drag";
        this.setCellFactory((Callback<TreeView<?>, TreeCell<?>>) param -> new RichTreeCell());
        // 初始化根节点
        super.setRoot(new ZKRootTreeItem(this));
        this.getRoot().expend();
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
    public ZKTreeItemFilter itemFilter() {
        try {
            // 初始化过滤器
            if (this.itemFilter == null) {
                ZKTreeItemFilter filter = new ZKTreeItemFilter();
                filter.initFilters();
                this.itemFilter = filter;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ZKTreeItemFilter) this.itemFilter;
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
        StageManager.showStage(ZKInfoAddController.class, this.window());
    }

    // @EventSubscribe
    // private void onDataOpened(ZKDataOpenedEvent event) {
    //     for (ZKConnectTreeItem item : this.getRoot().getConnectedItems()) {
    //         if (item.client() == event.data()) {
    //             ZKDataTreeItem treeItem = item.dataChild();
    //             if (treeItem != null) {
    //                 treeItem.stopWaiting();
    //             }
    //             break;
    //         }
    //     }
    // }
}
