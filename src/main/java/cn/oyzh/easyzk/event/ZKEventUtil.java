package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.msg.TreeChildChangedEvent;
import cn.oyzh.easyzk.event.msg.TreeChildFilterEvent;
import cn.oyzh.easyzk.event.msg.TreeChildSelectedEvent;
import cn.oyzh.easyzk.event.msg.TreeGraphicChangedEvent;
import cn.oyzh.easyzk.event.msg.TreeGraphicColorChangedEvent;
import cn.oyzh.easyzk.event.msg.ZKAuthMainEvent;
import cn.oyzh.easyzk.event.msg.ZKConnectionClosedEvent;
import cn.oyzh.easyzk.event.msg.ZKConnectionConnectedEvent;
import cn.oyzh.easyzk.event.msg.ZKConnectionLostEvent;
import cn.oyzh.easyzk.event.msg.ZKFilterMainEvent;
import cn.oyzh.easyzk.event.msg.ZKInfoDeletedEvent;
import cn.oyzh.easyzk.event.msg.ZKNodeAddEvent;
import cn.oyzh.easyzk.event.msg.ZKNodeAddedEvent;
import cn.oyzh.easyzk.event.msg.ZKNodeDeleteEvent;
import cn.oyzh.easyzk.event.msg.ZKNodeDeletedEvent;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdateEvent;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdatedEvent;
import cn.oyzh.easyzk.event.msg.ZKSearchCloseEvent;
import cn.oyzh.easyzk.event.msg.ZKSearchFinishEvent;
import cn.oyzh.easyzk.event.msg.ZKSearchOpenEvent;
import cn.oyzh.easyzk.event.msg.ZKSearchStartEvent;
import cn.oyzh.easyzk.event.msg.ZKTerminalCloseEvent;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
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
        ZKConnectionLostEvent msg = new ZKConnectionLostEvent();
        msg.data(client);
        EventUtil.post(msg);
    }

    /**
     * 连接关闭事件
     *
     * @param client zk客户端
     */
    public static void connectionClosed(ZKClient client) {
        ZKConnectionClosedEvent msg = new ZKConnectionClosedEvent();
        msg.data(client);
        EventUtil.post(msg);
    }

    /**
     * 连接成功事件
     *
     * @param client zk客户端
     */
    public static void connectionConnected(ZKClient client) {
        ZKConnectionConnectedEvent msg = new ZKConnectionConnectedEvent();
        msg.data(client);
        EventUtil.post(msg);
    }

    /**
     * 节点添加事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeAdd(ZKClient client, String path) {
        ZKNodeAddEvent msg = new ZKNodeAddEvent();
        msg.data(path);
        msg.info(client.zkInfo());
        EventUtil.post(msg);
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
        ZKNodeAddedEvent msg = new ZKNodeAddedEvent();
        ZKNode zkNode = new ZKNode();
        zkNode.stat(stat);
        zkNode.nodePath(nodePath);
        zkNode.nodeData(nodeData);
        msg.data(zkNode);
        msg.client(client);
        EventUtil.post(msg);
    }

    /**
     * 节点修改事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeUpdate(ZKClient client, String path) {
        ZKNodeUpdateEvent msg = new ZKNodeUpdateEvent();
        msg.data(path);
        msg.infoName(client.infoName());
        EventUtil.post(msg);
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
        ZKNodeUpdatedEvent msg = new ZKNodeUpdatedEvent();
        ZKNode zkNode = new ZKNode();
        zkNode.stat(stat);
        zkNode.nodePath(nodePath);
        zkNode.nodeData(nodeData);
        msg.data(zkNode);
        msg.client(client);
        EventUtil.post(msg);
    }


    /**
     * 节点删除事件
     *
     * @param client      zk客户端
     * @param path        路径
     * @param delChildren 是否删除子节点
     */
    public static void nodeDelete(ZKClient client, String path, boolean delChildren) {
        ZKNodeDeleteEvent msg = new ZKNodeDeleteEvent();
        msg.data(path);
        msg.delChildren(delChildren);
        msg.infoName(client.infoName());
        EventUtil.post(msg);
    }

    /**
     * 节点已删除事件
     *
     * @param client   zk客户端
     * @param stat     状态
     * @param nodePath 路径
     */
    public static void nodeDeleted(ZKClient client, Stat stat, String nodePath) {
        ZKNodeDeletedEvent msg = new ZKNodeDeletedEvent();
        ZKNode zkNode = new ZKNode();
        zkNode.stat(stat);
        zkNode.nodePath(nodePath);
        msg.data(zkNode);
        msg.client(client);
        EventUtil.post(msg);
    }

    /**
     * 连接已新增事件
     *
     * @param info zk信息
     */
    public static void infoAdded(ZKInfo info) {
        ZKInfoAddedEvent msg = new ZKInfoAddedEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 连接已修改事件
     *
     * @param info zk信息
     */
    public static void infoUpdated(ZKInfo info) {
        ZKInfoUpdatedEvent msg = new ZKInfoUpdatedEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 连接已删除事件
     *
     * @param info zk信息
     */
    public static void infoDeleted(ZKInfo info) {
        ZKInfoDeletedEvent msg = new ZKInfoDeletedEvent();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 图标变化事件
     */
    public static void graphicChanged(TreeItem<?> treeItem) {
        TreeGraphicChangedEvent msg = new TreeGraphicChangedEvent();
        msg.data(treeItem);
        EventUtil.postDelay(msg, 200);
    }

    /**
     * 图标颜色变化事件
     */
    public static void graphicColorChanged(TreeItem<?> treeItem) {
        TreeGraphicColorChangedEvent msg = new TreeGraphicColorChangedEvent();
        msg.data(treeItem);
        EventUtil.postDelay(msg, 200);
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
        TreeChildSelectedEvent msg = new TreeChildSelectedEvent();
        msg.data(item);
        EventUtil.post(msg);
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
        ZKSearchStartEvent msg = new ZKSearchStartEvent();
        msg.data(searchParam);
        EventUtil.post(msg);
    }

    /**
     * 搜索结束事件
     */
    public static void searchFinish(ZKSearchParam searchParam) {
        ZKSearchFinishEvent msg = new ZKSearchFinishEvent();
        msg.data(searchParam);
        EventUtil.post(msg);
    }

    /**
     * 搜索打开事件
     */
    public static void searchOpen() {
        EventUtil.post(new ZKSearchOpenEvent());
    }

    /**
     * 搜索关闭事件
     */
    public static void searchClose() {
        EventUtil.post(new ZKSearchCloseEvent());
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
        EventUtil.post(new ZkFilterAddedEvent());
    }
}
