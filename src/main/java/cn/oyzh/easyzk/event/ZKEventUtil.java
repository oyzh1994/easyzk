package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeTableItem;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.event.EventUtil;
import javafx.scene.control.TreeItem;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

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
        EventUtil.post(event);
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
        ZKConnectionSucceedEvent event = new ZKConnectionSucceedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 节点添加事件
     *
     * @param info zk客户端
     * @param path 路径
     */
    public static void nodeAdded(ZKInfo info, String path) {
        ZKNodeAddedEvent event = new ZKNodeAddedEvent();
        event.data(path);
        event.info(info);
        EventUtil.post(event);
    }

    // /**
    //  * 节点已添加事件
    //  *
    //  * @param client   zk客户端
    //  * @param stat     状态
    //  * @param nodeData 数据
    //  * @param nodePath 路径
    //  */
    // public static void nodeAdded(ZKClient client, Stat stat, byte[] nodeData, String nodePath) {
    //     ZKNodeAddedEvent event = new ZKNodeAddedEvent();
    //     ZKNode zkNode = new ZKNode();
    //     zkNode.stat(stat);
    //     zkNode.nodePath(nodePath);
    //     zkNode.nodeData(nodeData);
    //     event.data(zkNode);
    //     event.client(client);
    //     EventUtil.post(event);
    // }

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
        event.infoName(client.infoName());
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
        event.infoName(client.infoName());
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
     * @param info zk信息
     */
    public static void infoAdded(ZKInfo info) {
        ZKInfoAddedEvent event = new ZKInfoAddedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 连接已修改事件
     *
     * @param info zk信息
     */
    public static void infoUpdated(ZKInfo info) {
        ZKInfoUpdatedEvent event = new ZKInfoUpdatedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 连接已删除事件
     *
     * @param info zk信息
     */
    public static void infoDeleted(ZKInfo info) {
        ZKInfoDeletedEvent event = new ZKInfoDeletedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 图标变化事件
     */
    public static void graphicChanged(TreeItem<?> treeItem) {
        TreeGraphicChangedEvent event = new TreeGraphicChangedEvent();
        event.data(treeItem);
        EventUtil.postDelay(event, 200);
    }

    /**
     * 图标颜色变化事件
     */
    public static void graphicColorChanged(TreeItem<?> treeItem) {
        TreeGraphicColorChangedEvent event = new TreeGraphicColorChangedEvent();
        event.data(treeItem);
        EventUtil.postDelay(event, 200);
    }

    /**
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        EventUtil.postDelay(new TreeChildChangedEvent(), 200);
    }

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        EventUtil.postDelay(new TreeChildFilterEvent(), 200);
    }

    /**
     * 树节点选中事件
     */
    public static void treeChildSelected(ZKNodeTreeItem item) {
        TreeChildSelectedEvent event = new TreeChildSelectedEvent();
        event.data(item);
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
     * @param info zk信息
     */
    public static void terminalOpen(ZKInfo info) {
        ZKTerminalOpenEvent event = new ZKTerminalOpenEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 终端关闭事件
     *
     * @param info zk信息
     */
    public static void terminalClose(ZKInfo info) {
        ZKTerminalCloseEvent event = new ZKTerminalCloseEvent();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 认证主页事件
     */
    public static void authMain() {
        EventUtil.post(new ZKAuthMainEvent());
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        EventUtil.post(new ZKFilterMainEvent());
    }

    /**
     * 搜索开始事件
     */
    public static void searchStart(ZKSearchParam searchParam) {
        ZKSearchStartEvent event = new ZKSearchStartEvent();
        event.data(searchParam);
        EventUtil.post(event);
    }

    /**
     * 搜索结束事件
     */
    public static void searchFinish(ZKSearchParam searchParam) {
        ZKSearchFinishEvent event = new ZKSearchFinishEvent();
        event.data(searchParam);
        EventUtil.post(event);
    }

    /**
     * 搜索触发事件
     */
    public static void searchFire() {
        EventUtil.post(new ZKSearchFireEvent());
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
     * 触发认证添加事件
     *
     * @param auth 认证
     */
    public static void authAdded(@NonNull ZKAuth auth) {
        if (auth.getEnable()) {
            ZKAuthAddedEvent event = new ZKAuthAddedEvent();
            event.data(auth);
            EventUtil.post(event);
        }
    }

    /**
     * 触发认证启用事件
     *
     * @param auth 认证
     */
    public static void authEnabled(@NonNull ZKAuth auth) {
        if (auth.getEnable()) {
            ZKAuthEnabledEvent event = new ZKAuthEnabledEvent();
            event.data(auth);
            EventUtil.post(event);
        }
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
     * 展开左侧
     */
    public static void leftExtend() {
        EventUtil.post(new ZKLeftExtendEvent());
    }

    /**
     * 收缩左侧
     */
    public static void leftCollapse() {
        EventUtil.post(new ZKLeftCollapseEvent());
    }

    /**
     * 添加过滤配置
     */
    public static void filterAdded() {
        EventUtil.post(new ZKFilterAddedEvent());
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
    public static void historyShow(ZKNodeTreeTableItem item) {
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
    public static void historyRestore(byte[] data, ZKNodeTreeTableItem item) {
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
    public static void nodeSelected(ZKNodeTreeTableItem item) {
        ZKNodeSelectedEvent event = new ZKNodeSelectedEvent();
        event.data(item);
        EventUtil.post(event);
    }

    public static void nodeACLAdded(ZKInfo info) {
        ZKNodeACLAddedEvent event = new ZKNodeACLAddedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    public static void nodeACLUpdated(ZKInfo info) {
        ZKNodeACLUpdatedEvent event = new ZKNodeACLUpdatedEvent();
        event.data(info);
        EventUtil.post(event);
    }

    public static void treeItemChanged(TreeItem<?> item) {
        ZKTreeItemChangedEvent event = new ZKTreeItemChangedEvent();
        event.data(item);
        EventUtil.post(event);

    }
}
