package cn.oyzh.easyzk.util;

import cn.oyzh.common.util.FileUtil;
import cn.oyzh.common.util.MD5Util;
import cn.oyzh.easyzk.ZKConst;
import lombok.experimental.UtilityClass;

import java.io.File;

/**
 * zk缓存工具类
 *
 * @author oyzh
 * @since 2024-11-25
 */
@UtilityClass
public class ZKCacheUtil {

    private static String baseDir(int hashCode, String path) {
        return ZKConst.NODE_CACHE_PATH + hashCode + File.separator + MD5Util.md5Hex(path);
    }

    /**
     * 缓存未保存数据
     *
     * @param hashCode hash码
     * @param path     路径
     * @param data     数据
     * @return 缓存结果
     */
    public static boolean cacheUnsavedData(int hashCode, String path, byte[] data) {
        if (data != null) {
            try {
                String baseDir = baseDir(hashCode, path);
                String fileName = baseDir + ".unsaved";
                FileUtil.touch(fileName);
                FileUtil.writeBytes(data, fileName);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return deleteUnsavedData(hashCode, path);
    }

    /**
     * 加载未保存数据
     *
     * @param hashCode hash码
     * @param path     路径
     * @return 未保存数据
     */
    public static byte[] loadUnsavedData(int hashCode, String path) {
        try {
            String baseDir = baseDir(hashCode, path);
            String fileName = baseDir + ".unsaved";
            if (FileUtil.exist(fileName)) {
                return FileUtil.readBytes(fileName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 删除未保存数据
     *
     * @param hashCode hash码
     * @param path     路径
     */
    public static boolean deleteUnsavedData(int hashCode, String path) {
        try {
            String baseDir = baseDir(hashCode, path);
            String fileName = baseDir + ".unsaved";
            FileUtil.del(fileName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 是否有未保存数据
     *
     * @param hashCode hash码
     * @param path     路径
     * @return 结果
     */
    public static boolean hasUnsavedData(int hashCode, String path) {
        return getUnsavedDataSize(hashCode, path) > 0;
    }

    /**
     * 获取未保存数据长度
     *
     * @param hashCode hash码
     * @param path     路径
     * @return 未保存数据长度
     */
    public static long getUnsavedDataSize(int hashCode, String path) {
        try {
            String baseDir = baseDir(hashCode, path);
            String fileName = baseDir + ".unsaved";
            File file = new File(fileName);
            if (file.exists()) {
                return file.length();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }


    /**
     * 缓存节点数据
     *
     * @param hashCode hash码
     * @param path     路径
     * @param data     数据
     * @return 缓存结果
     */
    public static boolean cacheNodeData(int hashCode, String path, byte[] data) {
        if (data != null) {
            try {
                String baseDir = baseDir(hashCode, path);
                String fileName = baseDir + ".data";
                FileUtil.touch(fileName);
                FileUtil.writeBytes(data, fileName);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
        return deleteNodeData(hashCode, path);
    }

    /**
     * 加载节点数据
     *
     * @param hashCode hash码
     * @param path     路径
     * @return 节点数据
     */
    public static byte[] loadNodeData(int hashCode, String path) {
        try {
            String baseDir = baseDir(hashCode, path);
            String fileName = baseDir + ".data";
            if (FileUtil.exist(fileName)) {
                return FileUtil.readBytes(fileName);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 删除节点数据
     *
     * @param hashCode hash码
     * @param path     路径
     */
    public static boolean deleteNodeData(int hashCode, String path) {
        try {
            String baseDir = baseDir(hashCode, path);
            String fileName = baseDir + ".unsaved";
            FileUtil.del(fileName);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 是否有节点数据
     *
     * @param hashCode hash码
     * @param path     路径
     * @return 结果
     */
    public static boolean hasNodeData(int hashCode, String path) {
        return getUnsavedDataSize(hashCode, path) > 0;
    }

    /**
     * 获取节点数据长度
     *
     * @param hashCode hash码
     * @param path     路径
     * @return 节点数据长度
     */
    public static long getNodeDataSize(int hashCode, String path) {
        try {
            String baseDir = baseDir(hashCode, path);
            String fileName = baseDir + ".data";
            File file = new File(fileName);
            if (file.exists()) {
                return file.length();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}
