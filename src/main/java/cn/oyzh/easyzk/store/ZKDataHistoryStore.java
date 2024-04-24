package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKDataHistory;
import cn.oyzh.easyzk.dto.ZKDataHistoryVO;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * zk数据历史存储
 *
 * @author oyzh
 * @since 2024/04/23
 */
public class ZKDataHistoryStore extends ArrayFileStore<ZKDataHistory> {

    /**
     * 当前实例
     */
    public static final ZKDataHistoryStore INSTANCE = new ZKDataHistoryStore();

    {
        StaticLog.info("ZKDataHistoryStore charset:{}.", this.charset());
    }

    @Override
    public List<ZKDataHistory> load() {
        return Collections.emptyList();
    }

    private String filePath(ZKDataHistory history) {
        return this.fileBasePath(history.getInfoId()) + File.separator + DigestUtil.md5Hex(history.getPath()) + "_" + history.getSaveTime() + ".data";
    }

    private String fileBasePath(String infoId) {
        return ZKConst.STORE_PATH + "dataHistory" + File.separator + infoId;
    }

    @Override
    public synchronized boolean add(@NonNull ZKDataHistory history) {
        try {
            FileUtil.writeBytes(history.getData(), this.filePath(history));
        } catch (Exception e) {
            StaticLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKDataHistory history) {
        try {
            return FileUtil.del(this.filePath(history));
        } catch (Exception e) {
            StaticLog.warn("delete error,err:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 获取数据
     *
     * @param history 历史
     * @return 数据
     */
    public synchronized byte[] getData(@NonNull ZKDataHistory history) {
        try {
            return FileUtil.readBytes(this.filePath(history));
        } catch (Exception e) {
            StaticLog.warn("add error,err:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 查找数据
     *
     * @param params 参数
     * @return 数据
     */
    public List<ZKDataHistory> list(Map<String, Object> params) {
        String path = (String) params.get("path");
        String infoId = (String) params.get("infoId");
        List<ZKDataHistory> list = new ArrayList<>();
        String baseDir = this.fileBasePath(infoId);
        if (FileUtil.isDirectory(baseDir)) {
            File[] files = FileUtil.ls(baseDir);
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith(DigestUtil.md5Hex(path))) {
                        String fileName = file.getName();
                        String saveTime = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
                        ZKDataHistory history = new ZKDataHistoryVO();
                        history.setPath(path);
                        history.setInfoId(infoId);
                        history.dataSize(file.length());
                        history.setSaveTime(Long.parseLong(saveTime));
                        list.add(history);
                    }
                }
            }
        }
        return list.reversed();
    }

    @Override
    public synchronized Paging<ZKDataHistory> getPage(int limit, Map<String, Object> params) {
        String path = (String) params.get("path");
        String infoId = (String) params.get("infoId");
        List<ZKDataHistory> list = new ArrayList<>();
        String baseDir = this.fileBasePath(infoId);
        if (FileUtil.isDirectory(baseDir)) {
            File[] files = FileUtil.ls(baseDir);
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith(DigestUtil.md5Hex(path))) {
                        String fileName = file.getName();
                        String saveTime = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf("."));
                        ZKDataHistory history = new ZKDataHistoryVO();
                        history.setPath(path);
                        history.setInfoId(infoId);
                        history.dataSize(file.length());
                        history.setSaveTime(Long.parseLong(saveTime));
                        list.add(history);
                    }
                }
            }
        }
        return new Paging<>(list.reversed(), limit);
    }
}
