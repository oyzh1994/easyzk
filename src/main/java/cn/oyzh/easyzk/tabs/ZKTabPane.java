package cn.oyzh.easyzk.tabs;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.event.connect.ZKConnectOpenedEvent;
import cn.oyzh.easyzk.event.connection.ZKConnectionClosedEvent;
import cn.oyzh.easyzk.event.connection.ZKServerEvent;
import cn.oyzh.easyzk.event.history.ZKHistoryRestoreEvent;
import cn.oyzh.easyzk.event.query.ZKAddQueryEvent;
import cn.oyzh.easyzk.event.query.ZKOpenQueryEvent;
import cn.oyzh.easyzk.event.query.ZKQueryDeletedEvent;
import cn.oyzh.easyzk.event.query.ZKQueryRenamedEvent;
import cn.oyzh.easyzk.event.terminal.ZKTerminalCloseEvent;
import cn.oyzh.easyzk.event.terminal.ZKTerminalOpenEvent;
import cn.oyzh.easyzk.tabs.changelog.ZKChangelogTab;
import cn.oyzh.easyzk.tabs.home.ZKHomeTab;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.tabs.query.ZKQueryTab;
import cn.oyzh.easyzk.tabs.server.ZKServerTab;
import cn.oyzh.easyzk.tabs.terminal.ZKTerminalTab;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tabs.RichTabPane;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.fx.plus.event.FXEventListener;
import cn.oyzh.fx.plus.keyboard.KeyListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;

/**
 * zk切换面板
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKTabPane extends RichTabPane implements FXEventListener {

    @Override
    public void onNodeInitialize() {
        if (!FXEventListener.super.isNodeInitialize()) {
            FXEventListener.super.onNodeInitialize();
            // 刷新
            KeyListener.listenReleased(this, KeyCode.F5, keyEvent -> this.reload());
//            // 搜索
//            KeyHandler searchKeyHandler = new KeyHandler();
//            searchKeyHandler.handler(e -> {
//                if (this.getSelectedItem() instanceof ZKNodeTab nodeTab) {
//                    nodeTab.doSearch();
//                }
//            });
//            searchKeyHandler.keyCode(KeyCode.F);
//            if (OSUtil.isMacOS()) {
//                searchKeyHandler.metaDown(true);
//            } else {
//                searchKeyHandler.controlDown(true);
//            }
//            searchKeyHandler.keyType(KeyEvent.KEY_RELEASED);
//            KeyListener.addHandler(this, searchKeyHandler);
        }
    }

    @Override
    public void onNodeDestroy() {
        FXEventListener.super.onNodeDestroy();
        KeyListener.unListenReleased(this, KeyCode.F5);
    }

    @Override
    protected void initTabPane() {
        super.initTabPane();
        this.initHomeTab();
        // 监听tab
        this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    TaskManager.startDelay("zk:homeTab:flush", this::flushHomeTab, 100);
                }
            }
        });
    }

    /**
     * 刷新主页标签
     */
    private void flushHomeTab() {
        if (this.tabsEmpty()) {
            this.initHomeTab();
        } else if (this.tabsSize() > 1) {
            this.closeHomeTab();
        }
    }

    /**
     * 获取主页tab
     *
     * @return 主页tab
     */
    public ZKHomeTab getHomeTab() {
        return super.getTab(ZKHomeTab.class);
    }

    /**
     * 初始化主页tab
     */
    public void initHomeTab() {
        if (this.getHomeTab() == null) {
            super.addTab(new ZKHomeTab());
        }
    }

    /**
     * 关闭主页tab
     */
    public void closeHomeTab() {
        super.closeTab(ZKHomeTab.class);
    }

    /**
     * 获取终端tab
     *
     * @param client zk客户端
     * @return 终端tab
     */
    private ZKTerminalTab getTerminalTab(ZKClient client) {
        if (client != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof ZKTerminalTab terminalTab && terminalTab.client() == client) {
                    return terminalTab;
                }
            }
        }
        return null;
    }

    /**
     * 终端打开事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void terminalOpen(ZKTerminalOpenEvent event) {
        ZKClient client = event.data();
        ZKTerminalTab terminalTab = this.getTerminalTab(client);
        if (terminalTab == null) {
            terminalTab = new ZKTerminalTab(client);
            super.addTab(terminalTab);
        } else {
            terminalTab.flushGraphic();
        }
        if (!terminalTab.isSelected()) {
            this.select(terminalTab);
        }
    }

    /**
     * 终端关闭事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void terminalClose(ZKTerminalCloseEvent event) {
        try {
            // 寻找节点
            ZKTerminalTab terminalTab = this.getTerminalTab(event.data());
            // 移除节点
            if (terminalTab != null) {
                terminalTab.closeTab();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 更新日志事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void changelog(ChangelogEvent event) {
        ZKChangelogTab tab = this.getTab(ZKChangelogTab.class);
        if (tab == null) {
            tab = new ZKChangelogTab();
            super.addTab(tab);
        }
        if (!tab.isSelected()) {
            this.select(tab);
        }
    }

    private ZKNodeTab getNodeTab(ZKConnect zkConnect) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof ZKNodeTab tab1 && tab1.zkConnect() == zkConnect) {
                return tab1;
            }
        }
        return null;
    }

    /**
     * 连接打开事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void connectionOpened(ZKConnectOpenedEvent event) {
        ZKNodeTab connectTab = this.getNodeTab(event.connect());
        if (connectTab == null) {
            connectTab = new ZKNodeTab(event.data());
            super.addTab(connectTab);
        }
        connectTab.selectTab();
    }

    /**
     * 连接关闭事件
     *
     * @param event 事件
     */
    @EventSubscribe
    private void connectionClosed(ZKConnectionClosedEvent event) {
//        ZKNodeTab connectTab = this.getNodeTab(event.connect());
//        // 要检查连接和客户端是否相同
//        if (connectTab != null && connectTab.client() == event.data()) {
//            connectTab.closeTab();
//        }
//        // 检查连接是否相同
//        ZKTerminalTab terminalTab = this.getTerminalTab(event.data());
//        if (terminalTab != null) {
//            terminalTab.closeTab();
//        }
//        // 检查连接是否相同
//        ZKServerTab serverTab = this.getServerTab(event.data());
//        if (serverTab != null) {
//            serverTab.closeTab();
//        }
        List<Tab> tabs = new ArrayList<>(this.getTabs());
        for (Tab tab : tabs) {
            // 服务tab
            if (tab instanceof ZKServerTab serverTab && serverTab.zkConnect() == event.connect()) {
                serverTab.closeTab();
            } else if (tab instanceof ZKNodeTab nodeTab && nodeTab.zkConnect() == event.connect()) {// 连接tab
                nodeTab.closeTab();
            } else if (tab instanceof ZKTerminalTab terminalTab && terminalTab.zkConnect() == event.connect()) {// 终端tab
                terminalTab.closeTab();
            } else if (tab instanceof ZKQueryTab queryTab && queryTab.zkConnect() == event.connect()) {// 查询tab
                queryTab.closeTab();
            }
        }
    }

    /**
     * 恢复数据
     *
     * @param event 事件
     */
    @EventSubscribe
    private void restoreData(ZKHistoryRestoreEvent event) {
        ZKNodeTab connectTab = this.getNodeTab(event.connect());
        if (connectTab != null) {
            connectTab.restoreData(event.data());
        }
    }

    /**
     * 获取服务信息tab
     *
     * @param connect zk连接
     * @return 服务信息tab
     */
    private ZKServerTab getServerTab(ZKConnect connect) {
        if (connect != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof ZKServerTab serverTab && serverTab.zkConnect() == connect) {
                    return serverTab;
                }
            }
        }
        return null;
    }

    /**
     * 初始化服务信息tab
     *
     * @param event 事件
     */
    @EventSubscribe
    public void server(ZKServerEvent event) {
        ZKServerTab serverTab = this.getServerTab(event.zkConnect());
        if (serverTab == null) {
            serverTab = new ZKServerTab();
            serverTab.init(event.data());
            super.addTab(serverTab);
        } else {
            serverTab.flushGraphic();
        }
        if (!serverTab.isSelected()) {
            this.select(serverTab);
        }
    }

    /**
     * 获取查询tab
     *
     * @param query 查询
     * @return 查询tab
     */
    private ZKQueryTab getQueryTab(ZKQuery query) {
        if (query != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof ZKQueryTab queryTab && queryTab.query() == query) {
                    return queryTab;
                }
            }
        }
        return null;
    }

    /**
     * 添加查询
     *
     * @param event 事件
     */
    @EventSubscribe
    public void addQuery(ZKAddQueryEvent event) {
        ZKQueryTab queryTab = new ZKQueryTab(event.data(), null);
        super.addTab(queryTab);
        this.select(queryTab);
    }

    /**
     * 打开查询
     *
     * @param event 事件
     */
    @EventSubscribe
    public void openQuery(ZKOpenQueryEvent event) {
        ZKQueryTab queryTab = this.getQueryTab(event.data());
        if (queryTab == null) {
            queryTab = new ZKQueryTab(event.getClient(), event.data());
            super.addTab(queryTab);
        }
        if (!queryTab.isSelected()) {
            this.select(queryTab);
        }
    }

    /**
     * 查询更名
     *
     * @param event 事件
     */
    @EventSubscribe
    public void queryRenamed(ZKQueryRenamedEvent event) {
        ZKQueryTab queryTab = this.getQueryTab(event.data());
        if (queryTab != null) {
            queryTab.flushTitle();
        }
    }

    /**
     * 查询删除
     *
     * @param event 事件
     */
    @EventSubscribe
    public void queryDeleted(ZKQueryDeletedEvent event) {
        ZKQueryTab queryTab = this.getQueryTab(event.data());
        if (queryTab != null) {
            queryTab.closeTab();
        }
    }
}
