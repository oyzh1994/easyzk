package cn.oyzh.easyzk.tabs;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKAuthMainEvent;
import cn.oyzh.easyzk.event.ZKConnectOpenedEvent;
import cn.oyzh.easyzk.event.ZKConnectionClosedEvent;
import cn.oyzh.easyzk.event.ZKFilterMainEvent;
import cn.oyzh.easyzk.event.ZKHistoryRestoreEvent;
import cn.oyzh.easyzk.event.ZKTerminalCloseEvent;
import cn.oyzh.easyzk.event.ZKTerminalOpenEvent;
import cn.oyzh.easyzk.tabs.auth.ZKAuthTab;
import cn.oyzh.easyzk.tabs.changelog.ChangelogTab;
import cn.oyzh.easyzk.tabs.connect.ZKConnectTab;
import cn.oyzh.easyzk.tabs.filter.ZKFilterTab;
import cn.oyzh.easyzk.tabs.home.ZKHomeTab;
import cn.oyzh.easyzk.tabs.terminal.ZKTerminalTab;
import cn.oyzh.event.EventSubscribe;
import cn.oyzh.fx.gui.tabs.DynamicTabPane;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.fx.plus.event.FXEventListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;

/**
 * zk切换面板
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKTabPane extends DynamicTabPane implements FXEventListener {

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
     * @return 终端tab
     */
    private ZKTerminalTab getTerminalTab(ZKInfo info) {
        if (info != null) {
            for (Tab tab : this.getTabs()) {
                if (tab instanceof ZKTerminalTab terminalTab && terminalTab.info() == info) {
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
        ZKInfo info = event.data();
        ZKTerminalTab terminalTab = this.getTerminalTab(info);
        if (terminalTab == null) {
            terminalTab = new ZKTerminalTab(info);
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
        ChangelogTab tab = this.getTab(ChangelogTab.class);
        if (tab == null) {
            tab = new ChangelogTab();
            super.addTab(tab);
        }
        if (!tab.isSelected()) {
            this.select(tab);
        }
    }

    private ZKConnectTab getConnectTab(ZKInfo info) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof ZKConnectTab connectTab && connectTab.info() == info) {
                return connectTab;
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
        ZKConnectTab connectTab = this.getConnectTab(event.info());
        if (connectTab == null) {
            connectTab = new ZKConnectTab(event.data());
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
        ZKConnectTab connectTab = this.getConnectTab(event.info());
        if (connectTab != null) {
            connectTab.closeTab();
        }
        ZKTerminalTab terminalTab = this.getTerminalTab(event.info());
        if (terminalTab != null) {
            terminalTab.closeTab();
        }
    }

    /**
     * 获取认证tab
     *
     * @return 认证tab
     */
    public ZKAuthTab getAuthTab() {
        return super.getTab(ZKAuthTab.class);
    }

    /**
     * 初始化认证tab
     */
    @EventSubscribe
    private void authMain(ZKAuthMainEvent event) {
        ZKAuthTab tab = this.getAuthTab();
        if (tab == null) {
            tab = new ZKAuthTab();
            super.addTab(tab);
        }
        this.select(tab);
    }

    /**
     * 获取过滤tab
     *
     * @return 过滤tab
     */
    private ZKFilterTab getFilterTab() {
        return super.getTab(ZKFilterTab.class);
    }

    /**
     * 初始化过滤tab
     */
    @EventSubscribe
    private void filterMain(ZKFilterMainEvent event) {
        ZKFilterTab tab = this.getFilterTab();
        if (tab == null) {
            tab = new ZKFilterTab();
            super.addTab(tab);
        }
        this.select(tab);
    }

    /**
     * 恢复数据
     *
     * @param event 事件
     */
    @EventSubscribe
    private void restoreData(ZKHistoryRestoreEvent event) {
        ZKConnectTab connectTab = this.getConnectTab(event.info());
        if (connectTab != null) {
            connectTab.restoreData(event.data());
        }
    }
}
