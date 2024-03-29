package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.msg.TreeChildChangedMsg;
import cn.oyzh.easyzk.event.msg.TreeChildFilterMsg;
import cn.oyzh.easyzk.event.msg.TreeChildSelectedMsg;
import cn.oyzh.easyzk.event.msg.TreeGraphicChangedMsg;
import cn.oyzh.easyzk.event.msg.TreeGraphicColorChangedMsg;
import cn.oyzh.easyzk.event.msg.ZKAuthMainMsg;
import cn.oyzh.easyzk.event.msg.ZKConnectionClosedMsg;
import cn.oyzh.easyzk.event.msg.ZKConnectionConnectedMsg;
import cn.oyzh.easyzk.event.msg.ZKConnectionLostMsg;
import cn.oyzh.easyzk.event.msg.ZKFilterMainMsg;
import cn.oyzh.easyzk.event.msg.ZKInfoDeletedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeAddMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeAddedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeDeleteMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeDeletedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdateMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdatedMsg;
import cn.oyzh.easyzk.event.msg.ZKSearchCloseMsg;
import cn.oyzh.easyzk.event.msg.ZKSearchFinishMsg;
import cn.oyzh.easyzk.event.msg.ZKSearchOpenMsg;
import cn.oyzh.easyzk.event.msg.ZKSearchStartMsg;
import cn.oyzh.easyzk.event.msg.ZKTerminalCloseMsg;
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
        ZKConnectionLostMsg msg = new ZKConnectionLostMsg();
        msg.data(client);
        EventUtil.post(msg);
    }

    /**
     * 连接关闭事件
     *
     * @param client zk客户端
     */
    public static void connectionClosed(ZKClient client) {
        ZKConnectionClosedMsg msg = new ZKConnectionClosedMsg();
        msg.data(client);
        EventUtil.post(msg);
    }

    /**
     * 连接成功事件
     *
     * @param client zk客户端
     */
    public static void connectionConnected(ZKClient client) {
        ZKConnectionConnectedMsg msg = new ZKConnectionConnectedMsg();
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
        ZKNodeAddMsg msg = new ZKNodeAddMsg();
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
        ZKNodeAddedMsg msg = new ZKNodeAddedMsg();
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
        ZKNodeUpdateMsg msg = new ZKNodeUpdateMsg();
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
        ZKNodeUpdatedMsg msg = new ZKNodeUpdatedMsg();
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
        ZKNodeDeleteMsg msg = new ZKNodeDeleteMsg();
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
        ZKNodeDeletedMsg msg = new ZKNodeDeletedMsg();
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
        ZKInfoDeletedMsg msg = new ZKInfoDeletedMsg();
        msg.data(info);
        EventUtil.post(msg);
    }

    /**
     * 图标变化事件
     */
    public static void graphicChanged(TreeItem<?> treeItem) {
        TreeGraphicChangedMsg msg = new TreeGraphicChangedMsg();
        msg.data(treeItem);
        EventUtil.postDelay(msg, 200);
    }

    /**
     * 图标颜色变化事件
     */
    public static void graphicColorChanged(TreeItem<?> treeItem) {
        TreeGraphicColorChangedMsg msg = new TreeGraphicColorChangedMsg();
        msg.data(treeItem);
        EventUtil.postDelay(msg, 200);
    }

    /**
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        EventUtil.postDelay(new TreeChildChangedMsg(), 200);
    }

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        EventUtil.postDelay(new TreeChildFilterMsg(), 200);
    }

    /**
     * 树节点选中事件
     */
    public static void treeChildSelected(ZKNodeTreeItem item) {
        TreeChildSelectedMsg msg = new TreeChildSelectedMsg();
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
        ZKTerminalCloseMsg event = new ZKTerminalCloseMsg();
        event.data(info);
        EventUtil.post(event);
    }

    /**
     * 认证主页事件
     */
    public static void authMain() {
        EventUtil.post(new ZKAuthMainMsg());
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        EventUtil.post(new ZKFilterMainMsg());
    }

    /**
     * 搜索开始事件
     */
    public static void searchStart(ZKSearchParam searchParam) {
        ZKSearchStartMsg msg = new ZKSearchStartMsg();
        msg.data(searchParam);
        EventUtil.post(msg);
    }

    /**
     * 搜索结束事件
     */
    public static void searchFinish(ZKSearchParam searchParam) {
        ZKSearchFinishMsg msg = new ZKSearchFinishMsg();
        msg.data(searchParam);
        EventUtil.post(msg);
    }

    /**
     * 搜索打开事件
     */
    public static void searchOpen() {
        EventUtil.post(new ZKSearchOpenMsg());
    }

    /**
     * 搜索关闭事件
     */
    public static void searchClose() {
        EventUtil.post(new ZKSearchCloseMsg());
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
