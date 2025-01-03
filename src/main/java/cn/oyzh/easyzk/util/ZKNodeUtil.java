package cn.oyzh.easyzk.util;

import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.ArrayUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.RuntimeUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.i18n.I18nHelper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.zookeeper.KeeperException;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * zk节点工具类
 *
 * @author oyzh
 * @since 2020/11/10
 */
@UtilityClass
public class ZKNodeUtil {

    /**
     * 权限属性
     */
    public static final String ACL_PROPERTIES = "a";

    /**
     * 数据属性
     */
    public static final String DATA_PROPERTIES = "d";

    /**
     * 状态属性
     */
    public static final String STAT_PROPERTIES = "s";

    /**
     * 全部属性
     */
    public static final String FULL_PROPERTIES = ACL_PROPERTIES + DATA_PROPERTIES + STAT_PROPERTIES;

    /**
     * 获取zk节点
     *
     * @param path   路径
     * @param client zk操作器
     * @return zk节点
     */
    public static ZKNode getNode(@NonNull ZKClient client, @NonNull String path) throws Exception {
        return getNode(client, path, FULL_PROPERTIES);
    }

    /**
     * 获取zk节点
     *
     * @param path   路径
     * @param client zk操作器
     * @return zk节点
     */
    public static ZKNode getNode(@NonNull ZKClient client, @NonNull String path, @NonNull String properties) throws Exception {
        if (!path.contains("/")) {
            throw new ZKException("path:[" + path + "]" + I18nHelper.invalid());
        }
        long start = System.currentTimeMillis();
        // zk节点
        ZKNode node = new ZKNode();
        // 设置节点路径
        node.nodePath(path);
        // 异常
        AtomicReference<Exception> exceptionReference = new AtomicReference<>();
        // 设置zk状态
        if (properties.contains("s")) {
            try {
                node.stat(client.checkExists(path));
            } catch (KeeperException.NoAuthException ignored) {
            } catch (Exception ex) {
                exceptionReference.set(ex);
            }
        }
        // 设置zk访问控制
        if (properties.contains("a")) {
            try {
                node.acl(client.getACL(path));
            } catch (KeeperException.NoAuthException ignored) {
            } catch (Exception ex) {
                exceptionReference.set(ex);
            }
        }
        // 设置zk数据
        if (properties.contains("d")) {
            try {
                node.setNodeData(client.getData(path));
            } catch (KeeperException.NoAuthException ignored) {
            } catch (Exception ex) {
                exceptionReference.set(ex);
            }
        }
        // 抛出异常
        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
        // 设置加载时间
        long end = System.currentTimeMillis();
        long loadTime = end - start;
        node.loadTime((short) loadTime);
        // 返回节点
        return node;
    }

    /**
     * 刷新节点数据
     *
     * @param client zk客户端
     * @param node   zk节点
     */
    public void refreshData(@NonNull ZKClient client, @NonNull ZKNode node) throws Exception {
        long start = System.currentTimeMillis();
        node.setNodeData(client.getData(node.nodePath()));
        long end = System.currentTimeMillis();
        long loadTime = end - start;
        node.loadTime((short) loadTime);
    }

    /**
     * 刷新节点权限
     *
     * @param client zk客户端
     * @param node   zk节点
     */
    public void refreshAcl(@NonNull ZKClient client, @NonNull ZKNode node) throws Exception {
        node.acl(client.getACL(node.nodePath()));
    }

    /**
     * 刷新节点配额
     *
     * @param client zk客户端
     * @param node   zk节点
     */
    public void refreshQuota(@NonNull ZKClient client, @NonNull ZKNode node) throws Exception {
        node.quota(client.listQuota(node.nodePath()));
    }

    /**
     * 刷新节点状态
     *
     * @param client zk客户端
     * @param node   zk节点
     */
    public void refreshStat(@NonNull ZKClient client, @NonNull ZKNode node) throws Exception {
        node.stat(client.checkExists(node.nodePath()));
    }

    /**
     * 刷新节点
     *
     * @param client zk客户端
     * @param node   zk节点
     */
    public void refreshNode(@NonNull ZKClient client, @NonNull ZKNode node) throws Exception {
        ZKNode n = ZKNodeUtil.getNode(client, node.nodePath());
        node.copy(n);
    }

    /**
     * 是否被过滤
     *
     * @param nodePath 节点路径
     * @param filters  zk过滤配置列表
     * @return 结果
     */
    public static boolean isFiltered(String nodePath, List<ZKFilter> filters) {
        if (CollectionUtil.isEmpty(filters) || nodePath == null) {
            return false;
        }
        // 匹配结果
        for (ZKFilter filter : filters) {
            // 未启用，不处理
            if (!filter.isEnable()) {
                continue;
            }
            // 模糊匹配
            if (filter.isPartMatch() && nodePath.contains(filter.getKw())) {
                return true;
            }
            // 完全匹配
            if (nodePath.equalsIgnoreCase(filter.getKw())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取父路径
     *
     * @param path 当前路径
     * @return 当前路径的父路径
     */
    public static String getParentPath(String path) {
        if (StringUtil.isBlank(path) || !path.contains("/")) {
            return "";
        }
        if (path.equals("/")) {
            return "/";
        }
        int index = path.lastIndexOf("/");
        if (index == 0) {
            return "/";
        }
        return path.substring(0, index);
    }

    /**
     * 连接路径
     *
     * @param parentPath 父路径
     * @param childPath  子路径
     * @return 连接后的路径
     */
    public static String concatPath(@NonNull String parentPath, @NonNull String childPath) {
        if (parentPath.endsWith("/") && childPath.startsWith("/")) {
            return parentPath + childPath.substring(1);
        }
        if (parentPath.endsWith("/") || childPath.startsWith("/")) {
            return parentPath + childPath;
        }
        return parentPath + "/" + childPath;
    }

    /**
     * 获取名称
     *
     * @param path 路径
     * @return 名称
     */
    public static String getName(String path) {
        if (path == null || path.isEmpty()) {
            return "";
        }
        return "/".equals(path) ? "/" : ArrayUtil.last(path.split("/"));
    }

    /**
     * 获取子节点
     *
     * @param parentPath 父节点路径
     * @return 子节点列表
     */
    public static List<ZKNode> getChildNode(@NonNull ZKClient client, @NonNull String parentPath) throws Exception {
        return getChildNode(client, parentPath, FULL_PROPERTIES, null);
    }

    /**
     * 获取子节点
     *
     * @param client     zk客户端
     * @param parentPath 父节点路径
     * @param properties 查询属性
     * @param filter     过滤器
     * @return 子节点列表
     */
    public static List<ZKNode> getChildNode(@NonNull ZKClient client, @NonNull String parentPath, @NonNull String properties, Predicate<String> filter) throws Exception {
        List<ZKNode> list = new ArrayList<>();
        // 获取子节点
        List<String> children = client.getChildren(parentPath);
        // 为空，直接返回
        if (CollectionUtil.isEmpty(children)) {
            return Collections.emptyList();
        }
        // 处理器核心数量
        int pCount = RuntimeUtil.processorCount();
        // 性能较差机器上同步执行
        if (pCount < 4) {
            // 获取节点数据
            for (String sub : children) {
                // 对节点路径做处理
                String path = ZKNodeUtil.concatPath(parentPath, sub);
                // 获取节点
                if (filter == null || filter.test(path)) {
                    list.add(getNode(client, path, properties));
                }
            }
            children.clear();
        } else {// 性能较好机器上异步执行
            // 任务列表
            List<Callable<ZKNode>> tasks = new ArrayList<>(children.size());
            // 获取节点数据
            for (String sub : children) {
                // 对节点路径做处理
                String path = ZKNodeUtil.concatPath(parentPath, sub);
                // 获取节点
                if (filter == null || filter.test(path)) {
                    // 添加到任务列表
                    tasks.add(() -> getNode(client, path, properties));
                }
                // 分批获取，避免机器爆炸
                if (tasks.size() >= pCount) {
                    list.addAll(ThreadUtil.invoke(tasks));
                    tasks.clear();
                }
            }
            // 处理尾部数据
            if (!tasks.isEmpty()) {
                list.addAll(ThreadUtil.invoke(tasks));
            }
            tasks.clear();
            children.clear();
        }
        return list;
    }

    /**
     * 获取子节点
     *
     * @param client        zk客户端
     * @param parentPath    父节点路径
     * @param existingNodes 查询属性
     * @param limit         过滤器
     * @return 子节点列表
     */
    public static List<ZKNode> getChildNode(@NonNull ZKClient client, @NonNull String parentPath, List<String> existingNodes, int limit) throws Exception {
        List<ZKNode> list = new ArrayList<>();
        // 获取子节点
        List<String> children = client.getChildren(parentPath);
        // 为空，直接返回
        if (CollectionUtil.isEmpty(children)) {
            return Collections.emptyList();
        }
        // 处理器核心数量
        int pCount = RuntimeUtil.processorCount();
        // 已加载数量
        int count = 0;
        // 性能较差机器上同步执行
        if (pCount < 4) {
            // 获取节点数据
            for (String sub : children) {
                // 对节点路径做处理
                String path = ZKNodeUtil.concatPath(parentPath, sub);
                // 判断是否存在
                if (existingNodes.contains(path)) {
                    continue;
                }
                // 获取节点
                list.add(getNode(client, path, FULL_PROPERTIES));
                if (limit > 0 && count++ > limit) {
                    break;
                }
            }
//            children.clear();
        } else {// 性能较好机器上异步执行
            // 任务列表
            List<Callable<ZKNode>> tasks = new ArrayList<>(children.size());
            // 获取节点数据
            for (String sub : children) {
                // 对节点路径做处理
                String path = ZKNodeUtil.concatPath(parentPath, sub);
                // 判断是否存在
                if (existingNodes.contains(path)) {
                    continue;
                }
                // 添加到任务列表
                tasks.add(() -> getNode(client, path, FULL_PROPERTIES));
                if (limit > 0 && count++ > limit) {
                    break;
                }
                if (tasks.size() >= pCount) {
                    list.addAll(ThreadUtil.invoke(tasks));
                    tasks.clear();
                }
            }
            // 处理尾部数据
            if (!tasks.isEmpty()) {
                list.addAll(ThreadUtil.invoke(tasks));
            }
//            tasks.clear();
//            children.clear();
        }
        if (list.size() > limit) {
            return list.subList(0, limit);
        }
        return list;
    }

    public static String decodePath(String nodePath) {
        if (StringUtil.containsAny(nodePath, "%", "+")) {
            return URLDecoder.decode(nodePath, Charset.defaultCharset());
        }
        return nodePath;
    }

    /**
     * 递归获取zk节点
     *
     * @param path    路径
     * @param client  zk操作器
     * @param filter  过滤器
     * @param success 成功处理
     * @param error   错误处理
     */
    public static void loopNode(@NonNull ZKClient client, @NonNull String path, Predicate<String> filter, @NonNull BiConsumer<String, byte[]> success, BiConsumer<String, Exception> error) throws Exception {
        // 获取节点
        try {
            if (filter == null || filter.test(path)) {
                success.accept(path, client.getData(path));
            }
        } catch (Exception ex) {
            if (error != null) {
                error.accept(path, ex);
            }
        }
        // 获取子节点
        try {
            List<String> children = client.getChildren(path);
            for (String child : children) {
                String nPath = ZKNodeUtil.concatPath(path, child);
                loopNode(client, nPath, filter, success, error);
            }
        } catch (Exception ex) {
            if (error != null) {
                error.accept(path, ex);
            }
        }
    }
}
