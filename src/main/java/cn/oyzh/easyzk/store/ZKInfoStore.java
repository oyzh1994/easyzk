package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.util.FileStore;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKInfo;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * zk信息存储
 *
 * @author oyzh
 * @since 2020/5/23
 */
@Slf4j
public class ZKInfoStore extends FileStore<ZKInfo> {

    /**
     * 当前实例
     */
    public static final ZKInfoStore INSTANCE = new ZKInfoStore();

    /**
     * 已加载的zk节点
     */
    private final List<ZKInfo> zkInfos;

    {
        this.filePath(ZKConst.STORE_PATH + "zk_info.json");
        log.info("ZKInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
        this.zkInfos = this.load();
        for (ZKInfo zkInfo : this.zkInfos) {
            if (StrUtil.isBlank(zkInfo.getId())) {
                zkInfo.setId(UUID.fastUUID().toString(true));
                this.update(zkInfo);
            }
        }
    }

    @Override
    public synchronized List<ZKInfo> load() {
        if (this.zkInfos == null) {
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            List<ZKInfo> zkInfos = JSON.parseArray(text, ZKInfo.class);
            if (CollUtil.isNotEmpty(zkInfos)) {
                zkInfos = zkInfos.parallelStream().sorted().collect(Collectors.toList());
            }
            return zkInfos;
        }
        return this.zkInfos;
    }

    @Override
    public synchronized boolean add(@NonNull ZKInfo zkInfo) {
        try {
            if (!this.zkInfos.contains(zkInfo)) {
                // 添加到集合
                this.zkInfos.add(zkInfo);
                // 更新数据
                return this.save(this.zkInfos);
            }
        } catch (Exception e) {
            log.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKInfo zkInfo) {
        try {
            // 更新数据
            if (this.zkInfos.contains(zkInfo)) {
                return this.save(this.zkInfos);
            }
        } catch (Exception e) {
            log.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKInfo zkInfo) {
        try {
            // 删除数据
            if (this.zkInfos.remove(zkInfo)) {
                return this.save(this.zkInfos);
            }
        } catch (Exception e) {
            log.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Paging<ZKInfo> getPage(int limit, Map<String, Object> params) {
        // 加载数据
        List<ZKInfo> zkInfos = this.load();
        // 分页对象
        Paging<ZKInfo> paging = new Paging<>(zkInfos, limit);
        // 数据为空
        if (CollUtil.isNotEmpty(zkInfos)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StrUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                zkInfos = zkInfos.parallelStream().filter(z ->
                        z.getHost() != null && z.getHost().contains(kw)
                                || z.getName() != null && z.getName().toLowerCase().contains(kw)
                                || z.getRemark() != null && z.getRemark().toLowerCase().contains(kw)
                ).collect(Collectors.toList());
            }
            // 对数据排序
            zkInfos = zkInfos.parallelStream().sorted().collect(Collectors.toList());
            // 添加到分页数据
            paging.dataList(zkInfos);
        }
        return paging;
    }

    /**
     * 是否存在此zk信息
     *
     * @param zkInfo zk信息
     * @return 结果
     */
    public boolean exist(ZKInfo zkInfo) {
        if (zkInfo == null) {
            return false;
        }
        for (ZKInfo info : this.zkInfos) {
            if (info.compareTo(zkInfo) == 0 && info != zkInfo) {
                return true;
            }
        }
        return false;
    }
}
