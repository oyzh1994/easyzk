package cn.oyzh.easyzk.zk;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.dto.ZKClusterNode;
import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.easyzk.exception.ZKNoAdminPermException;
import cn.oyzh.easyzk.exception.ZKNoChildPermException;
import cn.oyzh.easyzk.exception.ZKNoCreatePermException;
import cn.oyzh.easyzk.exception.ZKNoDeletePermException;
import cn.oyzh.easyzk.exception.ZKNoReadPermException;
import cn.oyzh.easyzk.exception.ZKNoWritePermException;
import cn.oyzh.easyzk.store.ZKSettingJdbcStore;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.ssh.SSHForwardConfig;
import cn.oyzh.ssh.SSHForwarder;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Quotas;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.Version;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.cli.DelQuotaCommand;
import org.apache.zookeeper.cli.SetQuotaCommand;
import org.apache.zookeeper.client.FourLetterWordMain;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.flexible.QuorumVerifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * zk客户端封装
 *
 * @author oyzh
 * @since 2020/6/8
 */
@Accessors(fluent = true, chain = true)
public class ZKClient {

    /**
     * 最后的创建节点
     */
    private String lastCreate;

    /**
     * 最后的修改节点
     */
    private String lastUpdate;

    /**
     * 最后的删除节点
     */
    private String lastDelete;

    /**
     * zk信息
     */
    @Getter
    private final ZKConnect connect;

    /**
     * 树监听对象
     */
    private TreeCache treeCache;

    /**
     * ssh端口转发器
     */
    private SSHForwarder sshForwarder;

    /**
     * 是否已初始化
     */
    private volatile boolean initialized;

    /**
     * 重试策略
     */
    @Getter
    @Setter
    private RetryPolicy retryPolicy;

    /**
     * zk客户端
     */
    private CuratorFramework framework;

    /**
     * zk消息监听器
     */
    private volatile ZKTreeListener cacheListener;

    /**
     * 缓存选择器
     */
    private final ZKTreeCacheSelector cacheSelector = new ZKTreeCacheSelector();

    /**
     * 初始化状态监听器
     */
    private final TreeCacheListener initializedListener = (c, e) -> {
        if (e.getType() == TreeCacheEvent.Type.INITIALIZED) {
            // 设置状态
            this.initialized = true;
            // 移除自身监听器
            this.treeCache.getListenable().removeListener(this.initializedListener);
            // 关闭节点监听器
            if (!this.isEnableListen()) {
                this.closeTreeCache();
            }
        }
    };

    /**
     * 连接状态
     */
    private final ReadOnlyObjectWrapper<ZKConnState> state = new ReadOnlyObjectWrapper<>();

    /**
     * 获取连接状态
     *
     * @return 连接状态
     */
    public ZKConnState state() {
        return this.stateProperty().get();
    }

    /**
     * 连接状态属性
     *
     * @return 连接状态属性
     */
    public ReadOnlyObjectProperty<ZKConnState> stateProperty() {
        return this.state.getReadOnlyProperty();
    }

    public ZKClient(@NonNull ZKConnect connect) {
        this.connect = connect;
        if (connect.isSSHForward() && connect.getSshConnect() != null) {
            this.sshForwarder = new SSHForwarder(connect.getSshConnect());
        }
        this.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || !newValue.isConnected()) {
                this.closeTreeCache();
            } else {
                ThreadUtil.startVirtual(this::startTreeCache);
            }
            if (newValue != null) {
                switch (newValue) {
                    case LOST -> ZKEventUtil.connectionLost(this);
                    case CLOSED -> ZKEventUtil.connectionClosed(this);
                    case CONNECTED -> ZKEventUtil.connectionSucceed(this);
                }
            }
        });
    }

    /**
     * 是否只读模式
     *
     * @return 结果
     */
    public boolean isReadonly() {
        return this.connect.isReadonly();
    }

    /**
     * 如果只读模式不支持操作，则抛出异常
     */
    public void throwReadonlyException() {
        if (this.isReadonly()) {
            throw new ReadonlyOperationException();
        }
    }

    /**
     * 开启zk树监听
     */
    private void startTreeCache() {
        try {
            // 关闭旧的zk树监听
            this.closeTreeCache();
            // 创建zk树监听
            if (this.cacheListener != null) {
                this.treeCache = ZKTreeCacheUtil.build(this.framework, this.cacheListener.path(), this.cacheSelector);
                this.treeCache.getListenable().addListener(this.cacheListener);
                this.treeCache.getListenable().addListener(this.initializedListener);
                this.treeCache.start();
            } else if (!this.isEnableListen()) {// 未开启监听则只创建连接状态初始化监听器
                this.treeCache = ZKTreeCacheUtil.build(this.framework, this.cacheSelector);
                this.treeCache.getListenable().addListener(this.initializedListener);
                this.treeCache.start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 关闭zk树监听
     */
    private void closeTreeCache() {
        try {
            if (this.treeCache != null) {
                this.treeCache.close();
                this.treeCache = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 是否开启了节点监听
     *
     * @return 结果
     */
    public boolean isEnableListen() {
        return this.connect.getListen();
    }

    /**
     * 连接zk以监听模式
     */
    public void startWithListener() {
        if (this.isEnableListen()) {
            this.cacheListener = new ZKTreeListener(this);
        } else {
            this.cacheListener = null;
        }
        this.start();
    }

    /**
     * 连接zk
     */
    public void start() {
        this.start(this.connect.connectTimeOutMs());
    }

    /**
     * 连接zk
     *
     * @param timeout 超时时间
     */
    public void start(int timeout) {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }
        // 初始化客户端
        this.initClient();
        try {
            // 开始连接时间
            final AtomicLong starTime = new AtomicLong();
            // 设置连接监听事件
            this.framework.getConnectionStateListenable().addListener((c, s) -> {
                this.state.set(ZKConnState.valueOf(s));
                // 连接成功
                if (s == ConnectionState.CONNECTED) {
                    long endTime = System.currentTimeMillis();
                    JulLog.info("zkClient connected used:{}ms.", (endTime - starTime.get()));
                }
                JulLog.info("ConnectionState changed:{}==============================", s);
            });
            // 开始连接时间
            starTime.set(System.currentTimeMillis());
            // 更新连接状态
            this.state.set(ZKConnState.CONNECTING);
            // 开始连接
            this.framework.start();
            // 连接成功前阻塞线程
            if (this.framework.blockUntilConnected(timeout, TimeUnit.MILLISECONDS)) {
                // 更新连接状态
                this.state.set(ZKConnState.CONNECTED);
                // 设置认证信息为已认证
                if (ZKSettingJdbcStore.SETTING.isAutoAuth()) {
                    ZKAuthUtil.setAuthed(this, ZKAuthUtil.loadEnableAuths());
                }
            } else {// 连接未成功则关闭
                this._close();
                if (this.state.get() == ZKConnState.FAILED) {
                    this.state.set(null);
                } else {
                    this.state.set(ZKConnState.FAILED);
                }
            }
        } catch (Exception ex) {
            this.state.set(ZKConnState.FAILED);
            JulLog.warn("zkClient start error", ex);
            throw new ZKException(ex);
        }
    }

    /**
     * 初始化客户端
     */
    private void initClient() {
        // 连接地址
        String host;
        // ssh端口转发
        if (this.connect.isSSHForward()) {
            SSHForwardConfig forwardConfig = new SSHForwardConfig();
            forwardConfig.setHost(this.connect.hostIp());
            forwardConfig.setPort(this.connect.hostPort());
            int localPort = this.sshForwarder.forward(forwardConfig);
            // 连接信息
            host = "127.0.0.1:" + localPort;
        } else {// 直连
            // 连接信息
            host = this.connect.hostIp() + ":" + this.connect.hostPort();
        }
        // 认证信息列表
        List<AuthInfo> authInfos = List.of();
        // 开启自动认证
        if (ZKSettingJdbcStore.SETTING.isAutoAuth()) {
            // 加载已启用的认证
            authInfos = ZKAuthUtil.toAuthInfo(ZKAuthUtil.loadEnableAuths());
            JulLog.info("auto authorization, auths: {}.", authInfos);
        }
        // 重试策略
        if (this.retryPolicy == null) {
            this.retryPolicy = new RetryOneTime(3_000);
        }
        // 创建客户端
        this.framework = ZKClientUtil.build(host, this.retryPolicy, this.connect.connectTimeOutMs(), this.connect.sessionTimeOutMs(), authInfos, this.connect.compatibility34());
    }

    /**
     * 关闭zk
     */
    public void close() {
        this._close();
        this.state.set(ZKConnState.CLOSED);
    }

    /**
     * 关闭zk实际业务
     */
    private void _close() {
        try {
            if (this.framework != null) {
                TaskManager.startTimeout(this.framework::close, 500);
                this.framework = null;
            }
            // 销毁端口转发
            if (this.connect.isSSHForward()) {
                this.sshForwarder.destroy();
            }
            this.closeTreeCache();
            this.initialized = false;
            ZKAuthUtil.removeAuthed(this);
            JulLog.info("zkClient closed.");
        } catch (Exception ex) {
            JulLog.warn("zkClient close error.", ex);
        }
    }

    /**
     * 是否最后创建的节点
     *
     * @param path 节点路径
     * @return 结果
     */
    public boolean isLastCreate(String path) {
        return Objects.equals(this.lastCreate, path);
    }

    /**
     * 清除最后创建的节点
     */
    public void clearLastCreate() {
        this.lastCreate = null;
    }

    /**
     * 是否最后修改的节点
     *
     * @param path 节点路径
     * @return 结果
     */
    public boolean isLastUpdate(String path) {
        return Objects.equals(this.lastUpdate, path);
    }

    /**
     * 清除最后修改的节点
     */
    public void clearLastUpdate() {
        this.lastUpdate = null;
    }

    /**
     * 是否最后删除的节点
     *
     * @param path 节点路径
     * @return 结果
     */
    public boolean isLastDelete(String path) {
        return Objects.equals(this.lastDelete, path);
    }

    /**
     * 清除最后删除的节点
     */
    public void clearLastDelete() {
        this.lastDelete = null;
    }

    /**
     * zk是否已连接
     *
     * @return 结果
     */
    public boolean isClosed() {
        return this.state() == ZKConnState.CLOSED;
    }

    /**
     * zk是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        ZKConnState state = this.state.get();
        return state != null && state.isConnected();
    }

    /**
     * zk是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        if (this.framework == null || this.isConnected()) {
            return false;
        }
        if (this.framework.getState() == CuratorFrameworkState.LATENT) {
            return true;
        }
        return this.state() == ZKConnState.CONNECTING;
    }

    /**
     * 设置权限
     *
     * @param path    路径
     * @param aclList 权限列表
     * @return Stat 节点状态
     * @throws Exception 异常
     */
    public Stat setACL(@NonNull String path, @NonNull List<ACL> aclList) throws Exception {
        return this.setACL(path, aclList, null);
    }

    /**
     * 设置权限
     *
     * @param path    路径
     * @param aclList 权限列表
     * @param version 数据版本
     * @return Stat 节点状态
     * @throws Exception 异常
     */
    public Stat setACL(@NonNull String path, @NonNull List<ACL> aclList, Integer version) throws Exception {
        this.throwReadonlyException();
        try {
            return this.framework.setACL().withVersion(version == null ? -1 : version).withACL(aclList).forPath(path);
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoAdminPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 设置权限
     *
     * @param paths   路径列表
     * @param aclList 权限列表
     * @throws Exception 异常
     */
    public void setACL(@NonNull List<String> paths, @NonNull List<ACL> aclList) throws Exception {
        this.throwReadonlyException();
        String path = null;
        try {
            for (String s : paths) {
                path = s;
                this.framework.setACL().withVersion(-1).withACL(aclList).forPath(path);
            }
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoAdminPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 设置权限
     *
     * @param path 路径
     * @param acl  权限
     * @return Stat
     * @throws Exception 异常
     */
    public Stat addACL(@NonNull String path, @NonNull ACL acl) throws Exception {
        List<ACL> aclList = this.getACL(path);
        aclList.add(acl);
        return this.setACL(path, aclList);
    }

    /**
     * 设置权限
     *
     * @param path 路径
     * @param list 权限列表
     * @return Stat
     * @throws Exception 异常
     */
    public Stat addACL(@NonNull String path, @NonNull List<? extends ACL> list) throws Exception {
        List<ACL> aclList = this.getACL(path);
        aclList.addAll(list);
        return this.setACL(path, aclList);
    }

    /**
     * 设置权限
     *
     * @param path 路径
     * @param acl  权限列表
     * @return Stat
     * @throws Exception 异常
     */
    public Stat deleteACL(@NonNull String path, @NonNull ACL acl) throws Exception {
        List<ACL> aclList = this.getACL(path);
        aclList.remove(acl);
        return this.setACL(path, aclList);
    }

    /**
     * 添加认证信息
     *
     * @param user     用户名
     * @param password 密码
     * @throws Exception 异常
     */
    public void addAuth(@NonNull String user, @NonNull String password) throws Exception {
        ZooKeeper zooKeeper = this.framework.getZookeeperClient().getZooKeeper();
        String data = user + ":" + password;
        zooKeeper.addAuthInfo("digest", data.getBytes());
        ZKAuthUtil.setAuthed(this, user, password);
    }

    /**
     * 检查节点是否存在
     *
     * @param path 节点路径
     * @return 结果
     */
    public boolean exists(@NonNull String path) {
        try {
            return this.checkExists(path) != null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 创建节点及父节点
     *
     * @param path 节点路径
     * @param data 节点值
     */
    public String createIncludeParents(@NonNull String path, byte[] data) throws Exception {
        return this.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, null, CreateMode.PERSISTENT, true);
    }

    /**
     * 创建节点及父节点
     *
     * @param path       节点路径
     * @param data       节点值
     * @param createMode 节点模式
     */
    public String createIncludeParents(@NonNull String path, byte[] data, @NonNull CreateMode createMode) throws Exception {
        return this.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, null, createMode, true);
    }

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param data       节点值
     * @param createMode 创建模式
     */
    public String create(@NonNull String path, byte[] data, @NonNull CreateMode createMode) throws Exception {
        return this.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, null, createMode, false);
    }

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param data       节点值
     * @param aclList    权限集合
     * @param ttl        生存时间
     * @param createMode 创建模式
     * @param cParents   在需要的时候是否创建父节点
     */
    public String create(@NonNull String path, byte[] data, @NonNull List<ACL> aclList, Long ttl, @NonNull CreateMode createMode, boolean cParents) throws Exception {
        this.throwReadonlyException();
        String old = this.lastCreate;
        try {
            this.lastCreate = path;
            CreateBuilder builder = this.framework.create();
            if (cParents) {
                builder.creatingParentsIfNeeded();
            }
            if (ttl != null) {
                builder.withTtl(ttl);
            }
            builder.withMode(createMode).withACL(aclList);
            String nodePath;
            if (data == null) {
                nodePath = builder.forPath(path);
            } else {
                nodePath = builder.forPath(path, data);
            }
            return nodePath;
        } catch (Exception ex) {
            this.lastCreate = old;
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoCreatePermException(path);
            }
            throw ex;
        }
    }

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param data       节点值
     * @param aclList    权限集合
     * @param createMode 创建模式
     */
    public String create(@NonNull String path, String data, @NonNull List<ACL> aclList, @NonNull CreateMode createMode) throws Exception {
        return this.create(path, data, aclList, null, createMode, false);
    }

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param data       节点值
     * @param aclList    权限集合
     * @param createMode 创建模式
     * @param cParents   在需要的时候是否创建父节点
     */
    public String create(@NonNull String path, String data, @NonNull List<ACL> aclList, @NonNull CreateMode createMode, boolean cParents) throws Exception {
        return this.create(path, data, aclList, null, createMode, cParents);
    }

    /**
     * 创建节点
     *
     * @param path       节点路径
     * @param data       节点值
     * @param aclList    权限集合
     * @param ttl        生存时间
     * @param createMode 创建模式
     * @param cParents   在需要的时候是否创建父节点
     */
    public String create(@NonNull String path, String data, @NonNull List<ACL> aclList, Long ttl, @NonNull CreateMode createMode, boolean cParents) throws Exception {
        byte[] bytes = null;
        if (data != null) {
            bytes = data.getBytes();
        }
        return this.create(path, bytes, aclList, ttl, createMode, cParents);
    }

    /**
     * 获取子节点
     *
     * @param path 路径
     * @return 子节点列表
     */
    public List<String> getChildren(@NonNull String path) throws Exception {
        try {
            return this.framework.getChildren().forPath(path);
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoChildPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 获取子节点(异步)
     *
     * @param path     路径
     * @param callback 回调函数
     */
    public void getChildren(@NonNull String path, @NonNull BackgroundCallback callback) throws Exception {
        try {
            this.framework.getChildren().inBackground(callback).forPath(path);
        } catch (IllegalStateException ignore) {
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoChildPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 获取节点数据
     *
     * @param path 路径
     * @return 节点数据
     */
    public byte[] getData(@NonNull String path) throws Exception {
        try {
            return this.framework.getData().forPath(path);
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoReadPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 获取节点数据(异步)
     *
     * @param path     路径
     * @param callback 回调函数
     */
    public void getData(@NonNull String path, @NonNull BackgroundCallback callback) throws Exception {
        try {
            this.framework.getData().inBackground(callback).forPath(path);
        } catch (IllegalStateException ignore) {
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoReadPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 获取节点数据，字符串形式
     *
     * @param path 路径
     * @return 节点数据
     */
    public String getDataString(@NonNull String path) throws Exception {
        byte[] bytes = this.getData(path);
        if (bytes != null) {
            return new String(bytes);
        }
        return null;
    }

    /**
     * 获取节点权限
     *
     * @param path 路径
     * @return 权限数据
     */
    public List<ACL> getACL(@NonNull String path) throws Exception {
        try {
            return this.framework.getACL().forPath(path);
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoAdminPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 获取节点权限(异步)
     *
     * @param path     路径
     * @param callback 回调函数
     */
    public void getACL(@NonNull String path, @NonNull BackgroundCallback callback) throws Exception {
        try {
            this.framework.getACL().inBackground(callback).forPath(path);
        } catch (IllegalStateException ignore) {
        } catch (Exception ex) {
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoAdminPermException(path);
            }
            throw ex;
        }
    }

    /**
     * 设置节点数据
     *
     * @param path 路径
     * @param data 数据
     * @return Stat 状态
     */
    public Stat setData(@NonNull String path, String data) throws Exception {
        byte[] bytes = Objects.requireNonNullElse(data, "").getBytes();
        return this.setData(path, bytes, null);
    }

    /**
     * 设置节点数据
     *
     * @param path    路径
     * @param data    数据
     * @param version 版本
     * @return Stat 状态
     */
    public Stat setData(@NonNull String path, String data, Integer version) throws Exception {
        byte[] bytes = Objects.requireNonNullElse(data, "").getBytes();
        return this.setData(path, bytes, version);
    }

    /**
     * 设置节点数据
     *
     * @param path 路径
     * @param data 数据
     * @return Stat 状态
     */
    public Stat setData(@NonNull String path, byte @NonNull [] data) throws Exception {
        return this.setData(path, data, null);
    }

    /**
     * 设置节点数据
     *
     * @param path    路径
     * @param data    数据
     * @param version 版本
     * @return Stat 状态
     */
    public Stat setData(@NonNull String path, byte @NonNull [] data, Integer version) throws Exception {
        this.throwReadonlyException();
        String old = this.lastUpdate;
        try {
            this.lastUpdate = path;
            Stat stat = this.framework.setData().withVersion(version == null ? -1 : version).forPath(path, data);
            if (stat != null) {
                ZKEventUtil.nodeUpdated(this, path);
            }
            return stat;
        } catch (Exception ex) {
            this.lastUpdate = old;
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoWritePermException(path);
            }
            throw ex;
        }
    }

    /**
     * 同步节点数据
     *
     * @param path 路径
     */
    public void sync(@NonNull String path) throws Exception {
        this.throwReadonlyException();
        this.framework.sync().forPath(path);
    }

    /**
     * 删除节点
     *
     * @param path 路径
     */
    public void delete(@NonNull String path) throws Exception {
        this.delete(path, null, false);
    }

    /**
     * 删除节点
     *
     * @param path        路径
     * @param version     版本
     * @param delChildren 删除子节点
     */
    public void delete(@NonNull String path, Integer version, boolean delChildren) throws Exception {
        this.throwReadonlyException();
        String old = this.lastDelete;
        try {
            this.lastDelete = path;
            DeleteBuilder builder = this.framework.delete();
            builder.guaranteed().withVersion(version == null ? -1 : version);
            if (delChildren) {
                builder.deletingChildrenIfNeeded();
            }
            builder.forPath(path);
            ZKEventUtil.nodeDeleted(this, path, delChildren);
        } catch (Exception ex) {
            this.lastDelete = old;
            if (ex instanceof KeeperException.NoAuthException) {
                JulLog.error("path:{} NoAuth!", path);
                throw new ZKNoDeletePermException(path);
            }
            throw ex;
        }
    }

    /**
     * 获取状态
     *
     * @param path 路径
     * @return 状态
     */
    public Stat checkExists(@NonNull String path) throws Exception {
        if (this.framework == null) {
            return null;
        }
        return this.framework.checkExists().forPath(path);
    }

    /**
     * 获取状态(异步)
     *
     * @param path     路径
     * @param callback 回调函数
     */
    public void checkExists(@NonNull String path, @NonNull BackgroundCallback callback) throws Exception {
        try {
            this.framework.checkExists().inBackground(callback).forPath(path);
        } catch (IllegalStateException ignore) {
        }
    }

    /**
     * 创建配额
     *
     * @param path  路径
     * @param bytes 限制数据大小
     * @param num   限制子节点数量
     */
    public boolean createQuota(@NonNull String path, long bytes, int num) throws Exception {
        this.throwReadonlyException();
        if (path.equals("/")) {
            return false;
        }
        return SetQuotaCommand.createQuota(this.getZooKeeper(), path, bytes, num);
    }

    /**
     * 删除配额
     *
     * @param path  路径
     * @param bytes 删除数据大小配额
     * @param count 删除子节点数量配额
     */
    public boolean delQuota(@NonNull String path, boolean bytes, boolean count) throws Exception {
        this.throwReadonlyException();
        if (path.equals("/")) {
            return false;
        }
        return DelQuotaCommand.delQuota(this.getZooKeeper(), path, bytes, count);
    }

    /**
     * 列举配额
     *
     * @param path 路径
     */
    public StatsTrack listQuota(@NonNull String path) throws Exception {
        if (path.equals("/")) {
            return null;
        }
        String absolutePath = Quotas.quotaZookeeper + path + "/" + Quotas.limitNode;
        byte[] data = this.getData(absolutePath);
        return new StatsTrack(new String(data));
    }

    /**
     * 获取当前配置
     *
     * @return 配置
     */
    public QuorumVerifier getCurrentConfig() {
        return this.framework.getCurrentConfig();
    }

    /**
     * 获取集群服务列表
     *
     * @return 集群服务列表
     */
    public List<ZKClusterNode> clusterNodes() {
        List<ZKClusterNode> servers = new ArrayList<>();
        try {
            QuorumVerifier verifier = this.getCurrentConfig();
            // 老版本实现
            if (this.connect.compatibility34() || verifier == null) {
                if (this.exists("/zookeeper/config")) {
                    String data = this.getDataString("/zookeeper/config");
                    if (data != null) {
                        for (String str : data.lines().toList()) {
                            if (str.startsWith("server.")) {
                                servers.add(new ZKClusterNode(str));
                            }
                        }
                    }
                }
            } else {// 新版本实现
                Collection<QuorumPeer.QuorumServer> quorumServers = verifier.getVotingMembers().values();
                for (QuorumPeer.QuorumServer quorumServer : quorumServers) {
                    ZKClusterNode serverNode = new ZKClusterNode(quorumServer);
                    serverNode.setWeight(verifier.getWeight(quorumServer.id));
                    servers.add(serverNode);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return servers;
    }

    /**
     * 是否已初始化完成
     *
     * @return 结果
     */
    public boolean initialized() {
        return this.initialized && this.isConnected();
    }

    /**
     * 获取连接名称
     *
     * @return 连接名称
     */
    public String connectName() {
        return this.connect.getName();
    }

    /**
     * 获取zookeeper对象
     *
     * @return ZooKeeper
     * @throws Exception 异常
     */
    public ZooKeeper getZooKeeper() throws Exception {
        return this.framework.getZookeeperClient().getZooKeeper();
    }

    /**
     * 添加状态监听器
     *
     * @param stateListener 状态监听器
     */
    public void addStateListener(ChangeListener<ZKConnState> stateListener) {
        if (stateListener != null) {
            this.state.addListener(stateListener);
        }
    }

    public List<ZKEnvNode> localEnvNodes() {
        List<ZKEnvNode> list = new ArrayList<>();
        ZKEnvNode host = new ZKEnvNode("host", this.connect.getHost());
        ZKEnvNode connection = new ZKEnvNode("connection", this.connect.getName());
        ZKEnvNode version = new ZKEnvNode("sdkVersion", Version.getFullVersion());
        ZKEnvNode jdkVersion = new ZKEnvNode("jdkVersion", System.getProperty("java.vm.version"));
        list.add(host);
        list.add(version);
        list.add(connection);
        list.add(jdkVersion);
        return list;
    }

    public String envi() {
        try {
            return FourLetterWordMain.send4LetterWord(this.connect.hostIp(), this.connect.hostPort(), "envi");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ZKEnvNode> serverEnvNodes() {
        String envi = this.envi();
        if (envi != null) {
            List<ZKEnvNode> list = new ArrayList<>();
            envi.lines().skip(1).forEach(l -> {
                int index = l.indexOf("=");
                if (index != -1) {
                    String name = l.substring(0, index);
                    String value = l.substring(index + 1);
                    ZKEnvNode envNode = new ZKEnvNode(name, value);
                    list.add(envNode);
                }
            });
            return list;
        }
        return Collections.emptyList();
    }

    public String srvr() {
        try {
            return FourLetterWordMain.send4LetterWord(this.connect.hostIp(), this.connect.hostPort(), "srvr");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ZKEnvNode> srvrNodes() {
        String srvr = this.srvr();
        if (srvr != null) {
            List<ZKEnvNode> list = new ArrayList<>();
            srvr.lines().forEach(l -> {
                int index = l.indexOf(":");
                if (index != -1) {
                    String name = l.substring(0, index);
                    String value = l.substring(index + 1);
                    ZKEnvNode envNode = new ZKEnvNode(name, value);
                    list.add(envNode);
                }
            });
            return list;
        }
        return Collections.emptyList();
    }

    public String stat() {
        try {
            return FourLetterWordMain.send4LetterWord(this.connect.hostIp(), this.connect.hostPort(), "stat");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ZKEnvNode> statNodes() {
        String stat = this.stat();
        if (stat != null) {
            List<ZKEnvNode> list = new ArrayList<>();
            stat.lines().forEach(l -> {
                int index = l.indexOf("=");
                if (index != -1) {
                    String name = l.substring(0, index);
                    String value = l.substring(index + 1);
                    ZKEnvNode envNode = new ZKEnvNode(name, value);
                    list.add(envNode);
                }
            });
            return list;
        }
        return Collections.emptyList();
    }

    public String conf() {
        try {
            return FourLetterWordMain.send4LetterWord(this.connect.hostIp(), this.connect.hostPort(), "conf");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<ZKEnvNode> confNodes() {
        String conf = this.conf();
        if (conf != null) {
            List<ZKEnvNode> list = new ArrayList<>();
            conf.lines().forEach(l -> {
                int index = l.indexOf("=");
                if (index != -1) {
                    String name = l.substring(0, index);
                    String value = l.substring(index + 1);
                    ZKEnvNode envNode = new ZKEnvNode(name, value);
                    list.add(envNode);
                }
            });
            return list;
        }
        return Collections.emptyList();
    }

    public String iid() {
        return this.connect.getId();
    }
}
