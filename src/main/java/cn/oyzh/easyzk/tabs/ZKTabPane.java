package cn.oyzh.easyzk.tabs;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.ZKTerminalOpenEvent;
import cn.oyzh.easyzk.event.msg.TreeChildSelectedEvent;
import cn.oyzh.easyzk.event.msg.TreeGraphicChangedEvent;
import cn.oyzh.easyzk.event.msg.TreeGraphicColorChangedEvent;
import cn.oyzh.easyzk.event.msg.ZKAuthMainEvent;
import cn.oyzh.easyzk.event.msg.ZKConnectionClosedEvent;
import cn.oyzh.easyzk.event.msg.ZKTerminalCloseEvent;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.tabs.auth.ZKAuthTab;
import cn.oyzh.easyzk.tabs.filter.ZKFilterTab;
import cn.oyzh.easyzk.tabs.home.ZKHomeTab;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.tabs.terminal.ZKTerminalTab;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.tabs.DynamicTabPane;
import cn.oyzh.fx.plus.util.FXUtil;
import com.google.common.eventbus.Subscribe;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
                    if (c.wasAdded()) {
                        TaskManager.startDelay("zk:nodeTab:flush", this::flushNodeTab, 100);
                    }
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
     * 刷新节点标签
     */
    private void flushNodeTab() {
        // 获取设置
        ZKSetting setting = ZKSettingStore.SETTING;
        // 判断是否需要处理tab限制
        if (setting.isTabUnLimit()) {
            return;
        }
        // 获取全部节点tab
        List<ZKNodeTab> tabs = this.getNodeTabs();
        // 数据不满足限制要求，则直接忽略
        if (tabs.size() <= setting.getTabLimit()) {
            return;
        }
        // tab处理函数
        Consumer<List<ZKNodeTab>> func = tabList -> {
            // 数据满足限制要求才处理
            if (tabList.size() > setting.getTabLimit()) {
                // 进行排序
                tabList.sort((o1, o2) -> Comparator.comparingLong(ZKNodeTab::getOpenedTime).compare(o2, o1));
                // 跳过指定数量
                List<ZKNodeTab> list = tabList.stream().skip(setting.getTabLimit()).toList();
                // 移除tab
                if (!list.isEmpty()) {
                    FXUtil.runLater(() -> this.getTabs().removeAll(list));
                }
            }
        };
        // 限制全部连接
        if (setting.isAllTabLimitStrategy()) {
            func.accept(tabs);
        } else if (setting.isSingleTabLimitStrategy()) {// 限制单个连接
            // 分组处理
            Map<ZKClient, List<ZKNodeTab>> map = new HashMap<>();
            // 按分组添加到map
            for (ZKNodeTab tab : tabs) {
                List<ZKNodeTab> list = map.computeIfAbsent(tab.client(), k -> new ArrayList<>());
                list.add(tab);
            }
            // 处理值
            for (List<ZKNodeTab> tabList : map.values()) {
                func.accept(tabList);
            }
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
    public ZKTerminalTab getTerminalTab(ZKInfo info) {
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
     * 初始化终端tab
     *
     * @param info zk信息
     */
    public void initTerminalTab(ZKInfo info) {
        ZKTerminalTab terminalTab = this.getTerminalTab(info);
        if (terminalTab == null) {
            terminalTab = new ZKTerminalTab();
            terminalTab.init(info);
            super.addTab(terminalTab);
        } else {
            terminalTab.flushGraphic();
        }
        if (!terminalTab.isSelected()) {
            this.select(terminalTab);
        }
    }

    /**
     * 终端打开事件
     *
     * @param event 事件
     */
    // @EventReceiver(value = ZKEventTypes.ZK_OPEN_TERMINAL, async = true, verbose = true, fxThread = true)
    @Subscribe
    private void openTerminal(ZKTerminalOpenEvent event) {
        this.initTerminalTab(event.data());
    }

    /**
     * 终端关闭事件
     *
     * @param event 事件
     */
    // @EventReceiver(value = ZKEventTypes.ZK_CLOSE_TERMINAL, async = true, verbose = true, fxThread = true)
    @Subscribe
    private void closeTerminal(ZKTerminalCloseEvent event) {
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
     * 图标更换事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = ZKEventTypes.TREE_GRAPHIC_CHANGED, async = true, verbose = true)
    @Subscribe
    public void graphicChanged(TreeGraphicChangedEvent msg) {
        ZKNodeTab nodeTab = this.getNodeTab(msg.data());
        if (nodeTab != null) {
            nodeTab.flushGraphic();
            nodeTab.flushTitle();
        }
    }

    /**
     * 图标颜色更换事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = ZKEventTypes.TREE_GRAPHIC_COLOR_CHANGED, async = true, verbose = true)
    @Subscribe
    public void graphicColorChanged(TreeGraphicColorChangedEvent msg) {
        ZKNodeTab nodeTab = this.getNodeTab(msg.data());
        if (nodeTab != null) {
            nodeTab.flushGraphicColor();
        }
    }

    /**
     * 获取节点tab
     *
     * @param item 树节点
     * @return 节点tab
     */
    public ZKNodeTab getNodeTab(TreeItem<?> item) {
        for (Tab tab : this.getTabs()) {
            if (tab instanceof ZKNodeTab nodeTab && nodeTab.treeItem() == item) {
                return nodeTab;
            }
        }
        return null;
    }

    /**
     * 获取节点tab列表
     *
     * @return 节点tab列表
     */
    public List<ZKNodeTab> getNodeTabs() {
        List<ZKNodeTab> list = new ArrayList<>();
        for (Tab tab : this.getTabs()) {
            if (tab instanceof ZKNodeTab nodeTab) {
                list.add(nodeTab);
            }
        }
        return list;
    }

    /**
     * 初始化节点tab
     *
     * @param msg 消息
     */
    // @EventReceiver(value = ZKEventTypes.TREE_CHILD_SELECTED, async = true, verbose = true, fxThread = true)
    @Subscribe
    public void initNodeTab(TreeChildSelectedEvent msg) {
        if (msg != null && msg.data() != null) {
            ZKNodeTab nodeTab = this.getNodeTab(msg.data());
            if (nodeTab == null) {
                nodeTab = new ZKNodeTab();
                super.addTab(nodeTab);
            }
            // 选中节点
            this.select(nodeTab);
            // 初始化节点
            nodeTab.init(msg.data());
        }
    }

    /**
     * 连接关闭事件
     *
     * @param msg 消息
     */
    // @EventReceiver(value = ZKEventTypes.ZK_CONNECTION_CLOSED, async = true, verbose = true)
    @Subscribe
    public void connectionClosed(ZKConnectionClosedEvent msg) {
        for (ZKNodeTab nodeTab : this.getNodeTabs()) {
            if (nodeTab.client() == msg.data()) {
                nodeTab.closeTab();
            }
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
    // @EventReceiver(value = ZKEventTypes.ZK_AUTH_MAIN, async = true, verbose = true)
    @Subscribe
    public void initAuthTab(ZKAuthMainEvent event) {
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
    public ZKFilterTab getFilterTab() {
        return super.getTab(ZKFilterTab.class);
    }

    /**
     * 初始化过滤tab
     */
    // @EventReceiver(value = ZKEventTypes.ZK_FILTER_MAIN, async = true, verbose = true)
    @Subscribe
    public void initFilterTab(ZKAuthMainEvent event) {
        ZKFilterTab tab = this.getFilterTab();
        if (tab == null) {
            tab = new ZKFilterTab();
            super.addTab(tab);
        }
        this.select(tab);
    }
}
