//package cn.oyzh.easyzk.store;
//
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.crypto.digest.DigestUtil;
//import cn.oyzh.easyzk.ZKConst;
//import cn.oyzh.easyzk.domain.ZKDataHistory;
//import cn.oyzh.easyzk.dto.ZKDataHistoryVO;
//import cn.oyzh.common.dto.Paging;
//import cn.oyzh.common.log.JulLog;
//import cn.oyzh.common.store.ArrayFileStore;
//import lombok.NonNull;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//
///**
// * zk数据历史存储
// *
// * @author oyzh
// * @since 2024/04/23
// */
//@Deprecated
//public class ZKDataHistoryStore extends ArrayFileStore<ZKDataHistory> {
//
//    /**
//     * 最大历史数量
//     */
//    public static int His_Max_Size = 30;
//
//    /**
//     * 当前实例
//     */
//    public static final ZKDataHistoryStore INSTANCE = new ZKDataHistoryStore();
//
//    {
//        JulLog.info("ZKDataHistoryStore charset:{}.", this.charset());
//    }
//
//    @Override
//    public List<ZKDataHistory> load() {
//        return Collections.emptyList();
//    }
//
//    private String filePath(ZKDataHistory history) {
//        return this.fileBasePath(history.getInfoId()) + File.separator + DigestUtil.md5Hex(history.getPath()) + "_" + history.getSaveTime() + ".data";
//    }
//
//    private String fileBasePath(String infoId) {
//        return ZKConst.STORE_PATH + "dataHistory" + File.separator + infoId;
//    }
//
//    /**
//     * 清理超出限制的数据
//     *
//     * @param infoId 节点id
//     * @param path   路径
//     */
//    private void clearLimit(String infoId, String path) {
//        String baseDir = this.fileBasePath(infoId);
//        if (FileUtil.isDirectory(baseDir)) {
//            File[] files = FileUtil.ls(baseDir);
//            if (files != null) {
//                List<File> fileList;
//                if (StringUtil.isBlank(path)) {
//                    String pathDigest = DigestUtil.md5Hex(path);
//                    fileList = List.of(files).parallelStream().filter(f -> f.getName().startsWith(pathDigest))
//                            .sorted(Comparator.comparingLong(File::lastModified)).toList();
//                } else {
//                    fileList = List.of(files).parallelStream().sorted(Comparator.comparingLong(File::lastModified)).toList();
//                }
//                fileList.reversed().stream().skip(His_Max_Size).forEach(File::delete);
//            }
//        }
//    }
//
//    /**
//     * 清理数据
//     *
//     * @param infoId 连接id
//     */
//    public void clear(@NonNull String infoId) {
//        FileUtil.del(this.fileBasePath(infoId));
//    }
//
//
//    @Override
//    public synchronized boolean add(@NonNull ZKDataHistory history) {
//        try {
//            FileUtil.writeBytes(history.getData(), this.filePath(history));
//            this.clearLimit(history.getInfoId(), history.getPath());
//        } catch (Exception e) {
//            JulLog.warn("add error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    @Override
//    public synchronized boolean delete(@NonNull ZKDataHistory history) {
//        try {
//            return FileUtil.del(this.filePath(history));
//        } catch (Exception e) {
//            JulLog.warn("delete error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 获取数据
//     *
//     * @param history 历史
//     * @return 数据
//     */
//    public synchronized byte[] getData(@NonNull ZKDataHistory history) {
//        try {
//            return FileUtil.readBytes(this.filePath(history));
//        } catch (Exception e) {
//            JulLog.warn("getData error,err:{}", e.getMessage());
//        }
//        return null;
//    }
//
//    /**
//     * 查找数据
//     *
//     * @param params 参数
//     * @return 数据
//     */
//    public List<ZKDataHistory> list(Map<String, Object> params) {
//        String path = (String) params.get("path");
//        String infoId = (String) params.get("infoId");
//        List<ZKDataHistory> list = new ArrayList<>();
//        String baseDir = this.fileBasePath(infoId);
//        if (FileUtil.isDirectory(baseDir)) {
//            File[] files = FileUtil.ls(baseDir);
//            if (files != null) {
//                String pathDigest = DigestUtil.md5Hex(path);
//                for (File file : files) {
//                    if (file.getName().startsWith(pathDigest)) {
//                        String fileName = file.getName();
//                        String saveTime = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
//                        ZKDataHistory history = new ZKDataHistoryVO();
//                        history.setPath(path);
//                        history.setInfoId(infoId);
//                        // history.dataSize(file.length());
//                        history.setSaveTime(Long.parseLong(saveTime));
//                        list.add(history);
//                    }
//                }
//            }
//        }
//        return list.reversed();
//    }
//
//    @Override
//    public synchronized Paging<ZKDataHistory> getPage(int limit, Map<String, Object> params) {
//        String path = (String) params.get("path");
//        String infoId = (String) params.get("infoId");
//        List<ZKDataHistory> list = new ArrayList<>();
//        String baseDir = this.fileBasePath(infoId);
//        if (FileUtil.isDirectory(baseDir)) {
//            File[] files = FileUtil.ls(baseDir);
//            if (files != null) {
//                for (File file : files) {
//                    if (file.getName().startsWith(DigestUtil.md5Hex(path))) {
//                        String fileName = file.getName();
//                        String saveTime = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
//                        ZKDataHistory history = new ZKDataHistoryVO();
//                        history.setPath(path);
//                        history.setInfoId(infoId);
//                        // history.dataSize(file.length());
//                        history.setSaveTime(Long.parseLong(saveTime));
//                        list.add(history);
//                    }
//                }
//            }
//        }
//        return new Paging<>(list.reversed(), limit);
//    }
//}
