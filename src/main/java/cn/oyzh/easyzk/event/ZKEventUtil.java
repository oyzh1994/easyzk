package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.action.ZKClientActionArgument;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.event.auth.ZKAuthAuthedEvent;
import cn.oyzh.easyzk.event.client.ZKClientActionEvent;
import cn.oyzh.easyzk.event.connect.ZKAddConnectEvent;
import cn.oyzh.easyzk.event.connect.ZKConnectAddedEvent;
import cn.oyzh.easyzk.event.connect.ZKConnectDeletedEvent;
import cn.oyzh.easyzk.event.connect.ZKConnectOpenedEvent;
import cn.oyzh.easyzk.event.connect.ZKConnectUpdatedEvent;
import cn.oyzh.easyzk.event.connection.ZKConnectionClosedEvent;
import cn.oyzh.easyzk.event.connection.ZKConnectionConnectedEvent;
import cn.oyzh.easyzk.event.connection.ZKConnectionLostEvent;
import cn.oyzh.easyzk.event.connection.ZKServerEvent;
import cn.oyzh.easyzk.event.group.ZKAddGroupEvent;
import cn.oyzh.easyzk.event.group.ZKGroupAddedEvent;
import cn.oyzh.easyzk.event.group.ZKGroupDeletedEvent;
import cn.oyzh.easyzk.event.group.ZKGroupRenamedEvent;
import cn.oyzh.easyzk.event.history.ZKHistoryAddedEvent;
import cn.oyzh.easyzk.event.history.ZKHistoryRestoreEvent;
import cn.oyzh.easyzk.event.history.ZKHistoryShowEvent;
import cn.oyzh.easyzk.event.node.ZKNodeACLAddedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeACLUpdatedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeAddedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeChangedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeCreatedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeDeletedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeRemovedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeSelectedEvent;
import cn.oyzh.easyzk.event.node.ZKNodeUpdatedEvent;
import cn.oyzh.easyzk.event.query.ZKAddQueryEvent;
import cn.oyzh.easyzk.event.query.ZKOpenQueryEvent;
import cn.oyzh.easyzk.event.query.ZKQueryAddedEvent;
import cn.oyzh.easyzk.event.query.ZKQueryDeletedEvent;
import cn.oyzh.easyzk.event.query.ZKQueryRenamedEvent;
import cn.oyzh.easyzk.event.terminal.ZKTerminalCloseEvent;
import cn.oyzh.easyzk.event.terminal.ZKTerminalOpenEvent;
import cn.oyzh.easyzk.event.tree.ZKTreeItemChangedEvent;
import cn.oyzh.easyzk.event.window.ZKShowAddConnectEvent;
import cn.oyzh.easyzk.event.window.ZKShowExportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowImportDataEvent;
import cn.oyzh.easyzk.event.window.ZKShowMainEvent;
import cn.oyzh.easyzk.event.window.ZKShowNodeAddEvent;
import cn.oyzh.easyzk.event.window.ZKShowSettingEvent;
import cn.oyzh.easyzk.event.window.ZKShowTransportDataEvent;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.EventUtil;
import cn.oyzh.fx.gui.event.Layout1Event;
import cn.oyzh.fx.gui.event.Layout2Event;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import javafx.scene.control.TreeItem;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * zk事件工具
 *
 * @author oyzh
 * @since 2023/9/18
 */
@UtilityClass
public class ZKEventUtil {

    /**
     * 连接丢失事件
     *
     * @param item zk客户端
     */
    public static void connectionOpened(ZKConnectTreeItem item) {
        ZKConnectOpenedEvent event = new ZKConnectOpenedEvent();
        event.data(item);
        EventUtil.postSync(event);
    }

    /**
     * 连接丢失事件
     *
     * @param client zk客户端
     */
    public static void connectionLost(ZKClient client) {
        ZKConnectionLostEvent event = new ZKConnectionLostEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client zk客户端
     */
    public static void connectionClosed(ZKClient client) {
        ZKConnectionClosedEvent event = new ZKConnectionClosedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 连接成功事件
     *
     * @param client zk客户端
     */
    public static void connectionSucceed(ZKClient client) {
        ZKConnectionConnectedEvent event = new ZKConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 节点添加事件
     *
     * @param zkConnect zk连接
     * @param path      路径
     */
    public static void nodeAdded(ZKConnect zkConnect, String path) {
        ZKNodeAddedEvent event = new ZKNodeAddedEvent();
        event.data(path);
        event.zkConnect(zkConnect);
        EventUtil.post(event);
    }

    /**
     * 节点已添加事件
     *
     * @param client   zk客户端
     * @param nodePath 路径
     */
    public static void nodeCreated(ZKClient client, String nodePath) {
        ZKNodeCreatedEvent event = new ZKNodeCreatedEvent();
        event.data(ZKNodeUtil.decodePath(nodePath));
        event.client(client);
        EventUtil.post(event);
    }

    /**
     * 节点修改事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeUpdated(ZKClient client, String path) {
        ZKNodeUpdatedEvent event = new ZKNodeUpdatedEvent();
        event.data(path);
        event.infoName(client.connectName());
        EventUtil.post(event);
    }

    /**
     * 节点已修改事件
     *
     * @param client   zk客户端
     * @param nodePath 路径
     */
    public static void nodeChanged(ZKClient client, String nodePath) {
        ZKNodeChangedEvent event = new ZKNodeChangedEvent();
        event.data(ZKNodeUtil.decodePath(nodePath));
        event.client(client);
        EventUtil.post(event);
    }

    /**
     * 节点删除事件
     *
     * @param client      zk客户端
     * @param path        路径
     * @param delChildren 是否删除子节点
     */
    public static void nodeDeleted(ZKClient client, String path, boolean delChildren) {
        ZKNodeDeletedEvent event = new ZKNodeDeletedEvent();
        event.data(path);
        event.delChildren(delChildren);
        event.infoName(client.connectName());
        EventUtil.post(event);
    }

    /**
     * 节点已删除事件
     *
     * @param client   zk客户端
     * @param nodePath 路径
     */
    public static void nodeRemoved(ZKClient client, String nodePath) {
        ZKNodeRemovedEvent event = new ZKNodeRemovedEvent();
        event.data(ZKNodeUtil.decodePath(nodePath));
        event.client(client);
        EventUtil.post(event);
    }

    /**
     * 连接已新增事件
     *
     * @param zkConnect zk连接
     */
    public static void connectAdded(ZKConnect zkConnect) {
        ZKConnectAddedEvent event = new ZKConnectAddedEvent();
        event.data(zkConnect);
        EventUtil.post(event);
    }

    /**
     * 连接已修改事件
     *
     * @param zkConnect zk连接
     */
    public static void connectUpdated(ZKConnect zkConnect) {
        ZKConnectUpdatedEvent event = new ZKConnectUpdatedEvent();
        event.data(zkConnect);
        EventUtil.post(event);
    }

    /**
     * 连接已删除事件
     *
     * @param zkConnect zk连接
     */
    public static void connectDeleted(ZKConnect zkConnect) {
        ZKConnectDeletedEvent event = new ZKConnectDeletedEvent();
        event.data(zkConnect);
        EventUtil.post(event);
    }

    /**
     * 终端打开事件
     */
    public static void terminalOpen() {
        terminalOpen(null);
    }

    /**
     * 终端打开事件
     *
     * @param client zk客户端
     */
    public static void terminalOpen(ZKClient client) {
        ZKTerminalOpenEvent event = new ZKTerminalOpenEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 终端关闭事件
     *
     * @param client zk客户端
     */
    public static void terminalClose(ZKClient client) {
        ZKTerminalCloseEvent event = new ZKTerminalCloseEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 触发认证添加事件
     */
    public static void authAuthed(@NonNull ZKNodeTreeItem item, boolean success, String user, String password) {
        ZKAuthAuthedEvent event = new ZKAuthAuthedEvent();
        event.data(item);
        event.user(user);
        event.success(success);
        event.password(password);
        EventUtil.post(event);
    }

    /**
     * 添加分组
     */
    public static void addGroup() {
        EventUtil.post(new ZKAddGroupEvent());
    }

    /**
     * 添加连接
     */
    public static void addConnect() {
        EventUtil.post(new ZKAddConnectEvent());
    }

    /**
     * 更新日志事件
     */
    public static void changelog() {
        EventUtil.post(new ChangelogEvent());
    }

    /**
     * 显示历史
     *
     * @param item zk树节点
     */
    public static void historyShow(ZKNodeTreeItem item) {
        ZKHistoryShowEvent event = new ZKHistoryShowEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 恢复历史
     *
     * @param data 历史
     * @param item zk树节点
     */
    public static void historyRestore(byte[] data, ZKNodeTreeItem item) {
        ZKHistoryRestoreEvent event = new ZKHistoryRestoreEvent();
        event.data(data);
        event.item(item);
        EventUtil.post(event);
    }

    /**
     * 数据历史新增
     *
     * @param history 数据历史
     * @param item    zk树节点
     */
    public static void dataHistoryAdded(ZKDataHistory history, TreeItem<?> item) {
        ZKHistoryAddedEvent event = new ZKHistoryAddedEvent();
        event.data(history);
        event.item(item);
        EventUtil.post(event);
    }

    /**
     * zk节点选中事件
     *
     * @param item 节点
     */
    public static void nodeSelected(ZKNodeTreeItem item) {
        ZKNodeSelectedEvent event = new ZKNodeSelectedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 节点acl添加事件
     *
     * @param zkConnect zk连接
     */
    public static void nodeACLAdded(ZKConnect zkConnect, String nodePath) {
        ZKNodeACLAddedEvent event = new ZKNodeACLAddedEvent();
        event.data(zkConnect);
        event.nodePath(nodePath);
        EventUtil.post(event);
    }

    /**
     * 节点acl修改事件
     *
     * @param zkConnect zk连接
     */
    public static void nodeACLUpdated(ZKConnect zkConnect, String nodePath) {
        ZKNodeACLUpdatedEvent event = new ZKNodeACLUpdatedEvent();
        event.data(zkConnect);
        event.nodePath(nodePath);
        EventUtil.post(event);
    }

    /**
     * 节点选中事件
     *
     * @param item 节点
     */
    public static void treeItemChanged(TreeItem<?> item) {
        ZKTreeItemChangedEvent event = new ZKTreeItemChangedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    /**
     * 布局1
     */
    public static void layout1() {
        EventUtil.post(new Layout1Event());
    }

    /**
     * 布局2
     */
    public static void layout2() {
        EventUtil.post(new Layout2Event());
    }

    /**
     * 分组已添加
     */
    public static void groupAdded(String group) {
        ZKGroupAddedEvent event = new ZKGroupAddedEvent();
        event.data(group);
        EventUtil.post(event);
    }

    /**
     * 分组已删除
     */
    public static void groupDeleted(String group) {
        ZKGroupDeletedEvent event = new ZKGroupDeletedEvent();
        event.data(group);
        EventUtil.post(event);
    }

    /**
     * 分组已更名
     */
    public static void groupRenamed(String group, String oldName) {
        ZKGroupRenamedEvent event = new ZKGroupRenamedEvent();
        event.data(group);
        event.oldName(oldName);
        EventUtil.post(event);
    }

    /**
     * 客户端操作
     */
    public static void clientAction(String connectName, String action) {
        ZKClientActionEvent event = new ZKClientActionEvent();
        event.data(connectName);
        event.action(action);
        EventUtil.postAsync(event);
    }

//    /**
//     * 客户端操作
//     */
//    public static void clientAction(String connectName, String action, ZKClientActionArgument... arguments) {
//        ZKClientActionEvent event = new ZKClientActionEvent();
//        event.data(connectName);
//        event.action(action);
//        event.arguments(arguments);
//        EventUtil.postAsync(event);
//    }

    /**
     * 客户端操作
     */
    public static void clientAction(String connectName, String action, List<ZKClientActionArgument> arguments) {
        ZKClientActionEvent event = new ZKClientActionEvent();
        event.data(connectName);
        event.action(action);
        event.arguments(arguments);
        EventUtil.postAsync(event);
    }

    /**
     * 服务信息事件
     *
     * @param client zk客户端
     */
    public static void server(ZKClient client) {
        ZKServerEvent event = new ZKServerEvent();
        event.data(client);
        EventUtil.post(event);
    }

//    /**
//     * 搜索触发事件
//     *
//     * @param param     搜索参数
//     * @param zkConnect zk连接
//     */
//    public static void searchTrigger(ZKSearchParam param, ZKConnect zkConnect) {
//        ZKSearchTriggerEvent event = new ZKSearchTriggerEvent();
//        event.data(zkConnect);
//        event.param(param);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 搜索完成事件
//     *
//     * @param zkConnect zk连接
//     */
//    public static void searchComplete(ZKConnect zkConnect) {
//        ZKSearchCompleteEvent event = new ZKSearchCompleteEvent();
//        event.data(zkConnect);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 搜索结束事件
//     *
//     * @param zkConnect zk连接
//     */
//    public static void searchFinish(ZKConnect zkConnect) {
//        ZKSearchFinishEvent event = new ZKSearchFinishEvent();
//        event.data(zkConnect);
//        EventUtil.post(event);
//    }
//
//    /**
//     * 搜索关闭事件
//     *
//     * @param zkConnect zk连接
//     */
//    public static void searchClose(ZKConnect zkConnect) {
//        ZKSearchCloseEvent event = new ZKSearchCloseEvent();
//        event.data(zkConnect);
//        EventUtil.post(event);
//    }

    /**
     * 添加查询事件
     *
     * @param client zk查询
     */
    public static void addQuery(ZKClient client) {
        ZKAddQueryEvent event = new ZKAddQueryEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 查询已添加事件
     *
     * @param query zk查询
     */
    public static void queryAdded(ZKQuery query) {
        ZKQueryAddedEvent event = new ZKQueryAddedEvent();
        event.data(query);
        EventUtil.post(event);
    }

    /**
     * 查询打开事件
     *
     * @param client zk客户端
     * @param query  zk查询
     */
    public static void openQuery(ZKClient client, ZKQuery query) {
        ZKOpenQueryEvent event = new ZKOpenQueryEvent();
        event.data(query);
        event.setClient(client);
        EventUtil.post(event);
    }

    /**
     * 查询更名事件
     *
     * @param query zk查询
     */
    public static void queryRenamed(ZKQuery query) {
        ZKQueryRenamedEvent event = new ZKQueryRenamedEvent();
        event.data(query);
        EventUtil.post(event);
    }

    /**
     * 查询删除事件
     *
     * @param query zk查询
     */
    public static void queryDeleted(ZKQuery query) {
        ZKQueryDeletedEvent event = new ZKQueryDeletedEvent();
        event.data(query);
        EventUtil.post(event);
    }

    /**
     * 显示主页面
     */
    public static void showMain() {
        EventUtil.post(new ZKShowMainEvent());
    }

    /**
     * 显示设置页面
     */
    public static void showSetting() {
        EventUtil.post(new ZKShowSettingEvent());
    }

    /**
     * 显示传输数据页面
     */
    public static void showTransportData() {
        showTransportData(null);
    }

    /**
     * 显示传输数据页面
     *
     * @param connect zk连接
     */
    public static void showTransportData(ZKConnect connect) {
        ZKShowTransportDataEvent event = new ZKShowTransportDataEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 显示导出数据页面
     *
     * @param connect zk连接
     */
    public static void showExportData(ZKConnect connect, String path) {
        ZKShowExportDataEvent event = new ZKShowExportDataEvent();
        event.data(connect);
        event.path(path);
        EventUtil.post(event);
    }

    /**
     * 显示导入数据页面
     *
     * @param connect zk连接
     */
    public static void showImportData(ZKConnect connect) {
        ZKShowImportDataEvent event = new ZKShowImportDataEvent();
        event.data(connect);
        EventUtil.post(event);
    }

    /**
     * 显示添加连接页面
     */
    public static void showAddConnect() {
        showAddConnect(null);
    }

    /**
     * 显示添加连接页面
     *
     * @param group 分组
     */
    public static void showAddConnect(ZKGroup group) {
        ZKShowAddConnectEvent event = new ZKShowAddConnectEvent();
        event.data(group);
        EventUtil.post(event);
    }

    /**
     * 显示添加节点页面
     *
     * @param item   节点
     * @param client 客户端
     */
    public static void showNodeAdd(ZKNodeTreeItem item, ZKClient client) {
        ZKShowNodeAddEvent event = new ZKShowNodeAddEvent();
        event.data(item);
        event.client(client);
        EventUtil.post(event);
    }
}
