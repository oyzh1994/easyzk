package cn.oyzh.easyzk.tabs;

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
import cn.oyzh.easyzk.tabs.filter.ZKFilterTab;
import cn.oyzh.easyzk.tabs.home.ZKHomeTab;
import cn.oyzh.easyzk.tabs.node.ZKConnectTab;
import cn.oyzh.easyzk.tabs.terminal.ZKTerminalTab;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.tabs.DynamicTabPane;
import com.google.common.eventbus.Subscribe;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;

/**
 * zk切换面板
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKTabPane extends DynamicTabPane implements EventListener {

    @Override
    protected void initTabPane() {
        super.initTabPane();
        this.initHomeTab();
        // 监听tab
        this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
            while (c.next()) {
                if (c.wasAdded() || c.wasRemoved()) {
                    TaskManager.startDelay("zk:homeTab:flush", this::flushHomeTab, 100);
                    // if (c.wasAdded()) {
                    //     TaskManager.startDelay("zk:nodeTab:flush", this::flushNodeTab, 100);
                    // }
                }
            }
        });
        // // 监听ba变化
        // this.selectedTabChanged((observable, oldValue, newValue) -> ZKEventUtil.tabChanged(newValue));
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

    // /**
    //  * 刷新节点标签
    //  */
    // private void flushNodeTab() {
    //     // 获取设置
    //     ZKSetting setting = ZKSettingStore2.SETTING;
    //     // 判断是否需要处理tab限制
    //     if (setting.isTabUnLimit()) {
    //         return;
    //     }
    //     // 获取全部节点tab
    //     List<ZKNodeTab> tabs = this.getNodeTabs();
    //     // 数据不满足限制要求，则直接忽略
    //     if (tabs.size() <= setting.getTabLimit()) {
    //         return;
    //     }
    //     // tab处理函数
    //     Consumer<List<ZKNodeTab>> func = tabList -> {
    //         // 数据满足限制要求才处理
    //         if (tabList.size() > setting.getTabLimit()) {
    //             // 进行排序
    //             tabList.sort((o1, o2) -> Comparator.comparingLong(ZKNodeTab::getOpenedTime).compare(o2, o1));
    //             // 跳过指定数量
    //             List<ZKNodeTab> list = tabList.stream().skip(setting.getTabLimit()).toList();
    //             // 移除tab
    //             if (!list.isEmpty()) {
    //                 FXUtil.runLater(() -> this.getTabs().removeAll(list));
    //             }
    //         }
    //     };
    //     // 限制全部连接
    //     if (setting.isAllTabLimitStrategy()) {
    //         func.accept(tabs);
    //     } else if (setting.isSingleTabLimitStrategy()) {// 限制单个连接
    //         // 分组处理
    //         Map<ZKClient, List<ZKNodeTab>> map = new HashMap<>();
    //         // 按分组添加到map
    //         for (ZKNodeTab tab : tabs) {
    //             List<ZKNodeTab> list = map.computeIfAbsent(tab.client(), k -> new ArrayList<>());
    //             list.add(tab);
    //         }
    //         // 处理值
    //         for (List<ZKNodeTab> tabList : map.values()) {
    //             func.accept(tabList);
    //         }
    //     }
    // }

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
    @Subscribe
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
    @Subscribe
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
    @Subscribe
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
    @Subscribe
    private void connectionOpened(ZKConnectOpenedEvent event) {
        ZKConnectTab connectTab = this.getConnectTab(event.info());
        if (connectTab == null) {
            connectTab = new ZKConnectTab(event.data());
            connectTab.init(event.data());
            super.addTab(connectTab);
        }
        connectTab.selectTab();
    }

    /**
     * 连接关闭事件
     *
     * @param event 事件
     */
    @Subscribe
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
    @Subscribe
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
    @Subscribe
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
    @Subscribe
    private void restoreData(ZKHistoryRestoreEvent event) {
        ZKConnectTab connectTab = this.getConnectTab(event.info());
        if (connectTab != null) {
            connectTab.restoreData(event.data());
        }
    }
}
