package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKSearchParam;
import cn.oyzh.easyzk.event.msg.TreeChildChangedMsg;
import cn.oyzh.easyzk.event.msg.TreeChildFilterMsg;
import cn.oyzh.easyzk.event.msg.TreeGraphicChangedMsg;
import cn.oyzh.easyzk.event.msg.TreeGraphicColorChangedMsg;
import cn.oyzh.easyzk.event.msg.ZKAuthMainMsg;
import cn.oyzh.easyzk.event.msg.ZKConnectionClosedMsg;
import cn.oyzh.easyzk.event.msg.ZKConnectionConnectedMsg;
import cn.oyzh.easyzk.event.msg.ZKConnectionLostMsg;
import cn.oyzh.easyzk.event.msg.ZKFilterMainMsg;
import cn.oyzh.easyzk.event.msg.ZKInfoAddedMsg;
import cn.oyzh.easyzk.event.msg.ZKInfoDeletedMsg;
import cn.oyzh.easyzk.event.msg.ZKInfoUpdatedMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeAddMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeDeleteMsg;
import cn.oyzh.easyzk.event.msg.ZKNodeUpdateMsg;
import cn.oyzh.easyzk.event.msg.ZKSearchFinishMsg;
import cn.oyzh.easyzk.event.msg.ZKSearchStartMsg;
import cn.oyzh.easyzk.event.msg.ZKTerminalCloseMsg;
import cn.oyzh.easyzk.event.msg.ZKTerminalOpenMsg;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventBuilder;
import cn.oyzh.fx.plus.event.EventUtil;
import javafx.scene.control.TreeItem;
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
     * @param client zk客户端
     */
    public static void connectionLost(ZKClient client) {
        ZKConnectionLostMsg msg = new ZKConnectionLostMsg();
        msg.client(client);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 连接关闭事件
     *
     * @param client zk客户端
     */
    public static void connectionClosed(ZKClient client) {
        ZKConnectionClosedMsg msg = new ZKConnectionClosedMsg();
        msg.client(client);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 连接成功事件
     *
     * @param client zk客户端
     */
    public static void connectionConnected(ZKClient client) {
        ZKConnectionConnectedMsg msg = new ZKConnectionConnectedMsg();
        msg.client(client);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 节点添加事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeAdd(ZKClient client, String path) {
        ZKNodeAddMsg msg = new ZKNodeAddMsg();
        msg.path(path);
        msg.info(client.zkInfo());
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 节点修改事件
     *
     * @param client zk客户端
     * @param path   路径
     */
    public static void nodeUpdate(ZKClient client, String path) {
        ZKNodeUpdateMsg msg = new ZKNodeUpdateMsg();
        msg.path(path);
        msg.infoName(client.infoName());
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 连接已新增事件
     *
     * @param info zk信息
     */
    public static void infoAdded(ZKInfo info) {
        ZKInfoAddedMsg msg = new ZKInfoAddedMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 连接已修改事件
     *
     * @param info zk信息
     */
    public static void infoUpdated(ZKInfo info) {
        ZKInfoUpdatedMsg msg = new ZKInfoUpdatedMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 连接已删除事件
     *
     * @param info zk信息
     */
    public static void infoDeleted(ZKInfo info) {
        ZKInfoDeletedMsg msg = new ZKInfoDeletedMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
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
        msg.path(path);
        msg.delChildren(delChildren);
        msg.infoName(client.infoName());
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 图标变化事件
     */
    public static void graphicChanged(TreeItem<?> treeItem) {
        TreeGraphicChangedMsg msg = new TreeGraphicChangedMsg();
        msg.item(treeItem);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 图标颜色变化事件
     */
    public static void graphicColorChanged(TreeItem<?> treeItem) {
        TreeGraphicColorChangedMsg msg = new TreeGraphicColorChangedMsg();
        msg.item(treeItem);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 树节点变化事件
     */
    public static void treeChildChanged() {
        TreeChildChangedMsg msg = new TreeChildChangedMsg();
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 树节点过滤事件
     */
    public static void treeChildFilter() {
        TreeChildFilterMsg msg = new TreeChildFilterMsg();
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
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
        ZKTerminalOpenMsg msg = new ZKTerminalOpenMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 终端关闭事件
     *
     * @param info zk信息
     */
    public static void terminalClose(ZKInfo info) {
        ZKTerminalCloseMsg msg = new ZKTerminalCloseMsg();
        msg.info(info);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 认证主页事件
     */
    public static void authMain() {
        ZKAuthMainMsg msg = new ZKAuthMainMsg();
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 过滤主页事件
     */
    public static void filterMain() {
        ZKFilterMainMsg msg = new ZKFilterMainMsg();
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 搜索开始事件
     */
    public static void searchStart(ZKSearchParam searchParam) {
        ZKSearchStartMsg msg = new ZKSearchStartMsg();
        msg.searchParam(searchParam);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }

    /**
     * 搜索结束事件
     */
    public static void searchFinish(ZKSearchParam searchParam) {
        ZKSearchFinishMsg msg = new ZKSearchFinishMsg();
        msg.searchParam(searchParam);
        Event<Object> event = EventBuilder.newBuilder().type(msg.name()).group(msg.group()).data(msg).build();
        EventUtil.fire(event);
    }
}
