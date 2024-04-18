package cn.oyzh.easyzk.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.exception.ZKException;
import cn.oyzh.easyzk.exception.ZKNoAuthException;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.common.util.ArrUtil;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.zookeeper.KeeperException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * zk节点工具类
 *
 * @author oyzh
 * @since 2020/11/10
 */
@UtilityClass
public class ZKNodeUtil {

    /**
     * 数据缓存阈值
     */
    public static int Data_Cache_Threshold = 10_000;

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
    public static ZKNode getNode(@NonNull ZKClient client, @NonNull String path) {
        return getNode(client, path, FULL_PROPERTIES);
    }

    /**
     * 获取zk节点
     *
     * @param path   路径
     * @param client zk操作器
     * @return zk节点
     */
    public static ZKNode getNode(@NonNull ZKClient client, @NonNull String path, @NonNull String properties) {
        if (!path.contains("/")) {
            throw new ZKException("path:" + path + I18nResourceBundle.i18nString("base.invalid"));
        }
        try {
            long start = System.currentTimeMillis();
            // zk节点
            ZKNode node = new ZKNode();
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
                    node.nodeData(client.getData(path));
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
            // 设置节点路径
            node.nodePath(path);
            // 返回节点
            return node;
        } catch (Exception ex) {
            StaticLog.warn("getZKNode:{} error", path, ex);
        }
        return null;
    }

    /**
     * 刷新节点数据
     *
     * @param client zk客户端
     * @param node   zk节点
     */
    public void refreshData(@NonNull ZKClient client, @NonNull ZKNode node) throws Exception {
        long start = System.currentTimeMillis();
        node.nodeData(client.getData(node.nodePath()));
        long end = System.currentTimeMillis();
        long loadTime = end - start;
        node.loadTime((short) loadTime);
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
    public void refreshNode(@NonNull ZKClient client, @NonNull ZKNode node) {
        try {
            ZKNode n = ZKNodeUtil.getNode(client, node.nodePath());
            if (n != null) {
                node.copy(n);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 是否被过滤
     *
     * @param nodePath 节点路径
     * @param filters  zk过滤配置列表
     * @return 结果
     */
    public static boolean isFiltered(String nodePath, List<ZKFilter> filters) {
        if (CollUtil.isEmpty(filters) || nodePath == null) {
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
        if (StrUtil.isBlank(path) || !path.contains("/")) {
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
        return "/".equals(path) ? "/" : ArrUtil.last(path.split("/"));
    }

    /**
     * 缓存节点数据
     *
     * @param host 地址
     * @param path 路径
     * @param data 数据
     * @return 缓存结果
     */
    public static boolean cacheData(@NonNull String host, @NonNull String path, byte[] data) {
        try {
            if (data == null || data.length < Data_Cache_Threshold) {
                return false;
            }
            String baseDir = ZKConst.NODE_DATA_CACHE_PATH + DigestUtil.md5Hex(host) + File.separator;
            String fileName = baseDir + DigestUtil.md5Hex(path);
            FileUtil.writeBytes(data, fileName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 加载节点数据
     *
     * @param host 地址
     * @param path 路径
     * @return 节点数据
     */
    public static byte[] loadData(String host, String path) {
        try {
            String baseDir = ZKConst.NODE_DATA_CACHE_PATH + DigestUtil.md5Hex(host) + File.separator;
            String fileName = baseDir + DigestUtil.md5Hex(path);
            if (FileUtil.exist(fileName)) {
                return FileUtil.readBytes(fileName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 删除节点缓存
     *
     * @param host 地址
     * @param path 路径
     */
    public static void deleteData(String host, String path) {
        try {
            String baseDir = ZKConst.NODE_DATA_CACHE_PATH + DigestUtil.md5Hex(host) + File.separator;
            String fileName = baseDir + DigestUtil.md5Hex(path);
            FileUtil.del(fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取子节点
     *
     * @param parentPath 父节点路径
     * @return 子节点列表
     */
    public static List<ZKNode> getChildNode(@NonNull ZKClient client, @NonNull String parentPath) throws Exception {
        return getChildNode(client, parentPath, FULL_PROPERTIES);
    }

    /**
     * 获取子节点
     *
     * @param parentPath 父节点路径
     * @param properties 查询属性
     * @return 子节点列表
     */
    public static List<ZKNode> getChildNode(@NonNull ZKClient client, @NonNull String parentPath, @NonNull String properties) throws Exception {
        try {
            // 获取子节点
            List<String> children = client.getChildren(parentPath);
            // 为空，直接返回
            if (CollUtil.isEmpty(children)) {
                return Collections.emptyList();
            }
            // 任务列表
            List<Callable<ZKNode>> tasks = new ArrayList<>(children.size());
            // 获取节点数据
            for (String sub : children) {
                // 对节点路径做处理
                String path = ZKNodeUtil.concatPath(parentPath, sub);
                // 添加到任务列表
                tasks.add(() -> getNode(client, path, properties));
            }
            children.clear();
            // 返回数据
            return ThreadUtil.invoke(tasks);
        } catch (ZKNoAuthException ex) {
            return Collections.emptyList();
        }
    }
}
