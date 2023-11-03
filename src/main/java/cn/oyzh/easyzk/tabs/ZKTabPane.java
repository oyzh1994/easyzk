package cn.oyzh.easyzk.tabs;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventTypes;
import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import cn.oyzh.easyzk.msg.TreeGraphicChangedMsg;
import cn.oyzh.easyzk.msg.TreeGraphicColorChangedMsg;
import cn.oyzh.easyzk.msg.ZKConnectionClosedMsg;
import cn.oyzh.easyzk.msg.ZKTerminalCloseMsg;
import cn.oyzh.easyzk.msg.ZKTerminalOpenMsg;
import cn.oyzh.easyzk.tabs.auth.ZKAuthTab;
import cn.oyzh.easyzk.tabs.home.ZKHomeTab;
import cn.oyzh.easyzk.tabs.node.ZKNodeTab;
import cn.oyzh.easyzk.tabs.terminal.ZKTerminalTab;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.thread.TaskManager;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventReceiver;
import cn.oyzh.fx.plus.tabs.DynamicTabPane;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;

/**
 * zk切换面板
 *
 * @author oyzh
 * @since 2023/05/21
 */
public class ZKTabPane extends DynamicTabPane {

    {
        this.initHomeTab();
        this.getTabs().addListener((ListChangeListener<? super Tab>) (c) -> {
            TaskManager.startDelayTask("zk:tab:init", () -> {
                if (this.tabsEmpty()) {
                    this.initHomeTab();
                } else if (this.tabsSize() > 1) {
                    this.closeHomeTab();
                }
            }, 100);
        });
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
            ZKHomeTab tab = new ZKHomeTab();
            tab.init();
            super.addTab(tab);
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
     * zk终端打开事件
     *
     * @param event 事件
     */
    @EventReceiver(value = ZKEventTypes.ZK_OPEN_TERMINAL, async = true, verbose = true)
    private void openTerminal(Event<ZKTerminalOpenMsg> event) {
        this.initTerminalTab(event.data().info());
    }

    /**
     * zk终端关闭事件
     *
     * @param event 事件
     */
    @EventReceiver(value = ZKEventTypes.ZK_CLOSE_TERMINAL, async = true, verbose = true)
    private void closeTerminal(Event<ZKTerminalCloseMsg> event) {
        try {
            // 寻找节点
            ZKTerminalTab terminalTab = this.getTerminalTab(event.data().info());
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
    @EventReceiver(value = ZKEventTypes.TREE_GRAPHIC_CHANGED, async = true, verbose = true)
    public void graphicChanged(TreeGraphicChangedMsg msg) {
        ZKNodeTab nodeTab = this.getNodeTab();
        if (nodeTab != null && nodeTab.treeItem() == msg.item()) {
            nodeTab.flushGraphic();
        }
    }

    /**
     * 图标颜色更换事件
     *
     * @param msg 消息
     */
    @EventReceiver(value = ZKEventTypes.TREE_GRAPHIC_COLOR_CHANGED, async = true, verbose = true)
    public void graphicColorChanged(TreeGraphicColorChangedMsg msg) {
        ZKNodeTab nodeTab = this.getNodeTab();
        if (nodeTab != null && nodeTab.treeItem() == msg.item()) {
            nodeTab.flushGraphicColor();
        }
    }

    /**
     * 获取节点tab
     *
     * @return 节点tab
     */
    public ZKNodeTab getNodeTab() {
        return super.getTab(ZKNodeTab.class);
    }

    /**
     * 初始化节点tab
     *
     * @param item 节点
     */
    public void initNodeTab(ZKNodeTreeItem item) {
        if (item != null) {
            ZKNodeTab nodeTab = this.getNodeTab();
            if (nodeTab == null) {
                nodeTab = new ZKNodeTab();
                nodeTab.init(item);
                super.addTab(nodeTab);
            } else {
                nodeTab.init(item);
            }
            if (!nodeTab.isSelected()) {
                this.select(nodeTab);
            }
            // 检查节点状态
            ZKNodeTab finalNodeTab = nodeTab;
            ExecutorUtil.start(() -> FXUtil.runLater(finalNodeTab::checkStatus), 5);
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
    @EventReceiver(value = ZKEventTypes.ZK_AUTH_MAIN, async = true, verbose = true)
    public void initAuthTab() {
        ZKAuthTab tab = this.getAuthTab();
        if (tab == null) {
            tab = new ZKAuthTab();
            tab.init();
            super.addTab(tab);
        }
        if (!tab.isSelected()) {
            this.select(tab);
        }
    }

    /**
     * 连接关闭事件
     *
     * @param event 事件
     */
    @EventReceiver(value = ZKEventTypes.ZK_CONNECTION_CLOSED, async = true, verbose = true)
    public void connectionClosed(Event<ZKConnectionClosedMsg> event) {
        ZKConnectionClosedMsg msg = event.data();
        ZKNodeTab nodeTab = this.getNodeTab();
        if (nodeTab != null && nodeTab.info() == msg.info()) {
            nodeTab.closeTab();
        }
    }
}
