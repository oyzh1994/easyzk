package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.changelog.ChangelogEvent;
import cn.oyzh.fx.plus.event.EventUtil;
import javafx.scene.control.TreeItem;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.zookeeper.data.Stat;

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
    public static void connectionConnected(ZKClient client) {
        ZKConnectionConnectedEvent event = new ZKConnectionConnectedEvent();
        event.data(client);
        EventUtil.post(event);
    }

    /**
     * 节点添加事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeAdd(ZKClient client, String path) {
        ZKNodeAddEvent event = new ZKNodeAddEvent();
        event.data(path);
        event.info(client.zkInfo());
        EventUtil.post(event);
    }

    /**
     * 节点已添加事件
     *
     * @param client   zk客户端
     * @param stat     状态
     * @param nodeData 数据
     * @param nodePath 路径
     */
    public static void nodeAdded(ZKClient client, Stat stat, byte[] nodeData, String nodePath) {
        ZKNodeAddedEvent event = new ZKNodeAddedEvent();
        ZKNode zkNode = new ZKNode();
        zkNode.stat(stat);
        zkNode.nodePath(nodePath);
        zkNode.nodeData(nodeData);
        event.data(zkNode);
        event.client(client);
        EventUtil.post(event);
    }

    /**
     * 节点修改事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeUpdate(ZKClient client, String path) {
        ZKNodeUpdateEvent event = new ZKNodeUpdateEvent();
        event.data(path);
        event.infoName(client.infoName());
        EventUtil.post(event);
    }

    /**
     * 节点已修改事件
     *
     * @param client   zk客户端
     * @param stat     状态
     * @param nodeData 数据
     * @param nodePath 路径
     */
    public static void nodeUpdated(ZKClient client, Stat stat, byte[] nodeData, String nodePath) {
        ZKNodeUpdatedEvent event = new ZKNodeUpdatedEvent();
        ZKNode zkNode = new ZKNode();
        zkNode.stat(stat);
        zkNode.nodePath(nodePath);
        zkNode.nodeData(nodeData);
        event.data(zkNode);
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
    public static void nodeDelete(ZKClient client, String path, boolean delChildren) {
        ZKNodeDeleteEvent event = new ZKNodeDeleteEvent();
        event.data(path);
        event.delChildren(delChildren);
        event.infoName(client.infoName());
        EventUtil.post(event);
    }

    /**
     * 节点已删除事件
     *
     * @param client   zk客户端
     * @param stat     状态
     * @param nodePath 路径
     */
    public static void nodeDeleted(ZKClient client, Stat stat, String nodePath) {
        ZKNodeDeletedEvent event = new ZKNodeDeletedEvent();
        ZKNode zkNode = new ZKNode();
        zkNode.stat(stat);
        zkNode.nodePath(nodePath);
        event.data(zkNode);
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
    public static void authAuthed(@NonNull ZKNodeTreeItem item, boolean result, String user, String password) {
        ZKAuthAuthedEvent event = new ZKAuthAuthedEvent();
        event.data(item);
        event.user(user);
        event.result(result);
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
}
