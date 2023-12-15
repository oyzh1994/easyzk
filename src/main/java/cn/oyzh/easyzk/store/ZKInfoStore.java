package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

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
//@Slf4j
public class ZKInfoStore extends ArrayFileStore<ZKInfo> {

    /**
     * 当前实例
     */
    public static final ZKInfoStore INSTANCE = new ZKInfoStore();

    /**
     * 已加载的zk节点
     */
    private final List<ZKInfo> infos;

    {
        this.filePath(ZKConst.STORE_PATH + "zk_info.json");
        StaticLog.info("ZKInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
        this.infos = this.load();
        for (ZKInfo zkInfo : this.infos) {
            if (StrUtil.isBlank(zkInfo.getId())) {
                zkInfo.setId(UUID.fastUUID().toString(true));
                this.update(zkInfo);
            }
        }
    }

    @Override
    public synchronized List<ZKInfo> load() {
        // 如果zkInfos为空
        if (this.infos == null) {
            // 读取storeFile文件的内容
            String text = FileUtil.readString(this.storeFile(), this.charset());
            // 如果文件内容为空
            if (StrUtil.isBlank(text)) {
                // 返回空列表
                return new ArrayList<>();
            }
            // 将文件内容解析为ZKInfo列表
            List<ZKInfo> infos = JSONUtil.toList(text, ZKInfo.class);
            // 如果ZKInfo列表非空
            if (CollUtil.isNotEmpty(infos)) {
                // 对ZKInfo列表进行排序
                infos = infos.parallelStream().sorted().collect(Collectors.toList());
            }
            // 返回排序后的ZKInfo列表
            return infos;
        }
        // 返回已有的ZKInfo列表
        return this.infos;
    }

    @Override
    public synchronized boolean add(@NonNull ZKInfo zkInfo) {
        try {
            if (!this.infos.contains(zkInfo)) {
                if (StrUtil.isBlank(zkInfo.getId())) {
                    zkInfo.setId(UUID.fastUUID().toString(true));
                }
                // 添加到集合
                this.infos.add(zkInfo);
                // 更新数据
                return this.save(this.infos);
            }
        } catch (Exception e) {
            StaticLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKInfo zkInfo) {
        try {
            // 更新数据
            if (this.infos.contains(zkInfo)) {
                return this.save(this.infos);
            }
        } catch (Exception e) {
            StaticLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKInfo zkInfo) {
        try {
            // 删除数据
            if (this.infos.remove(zkInfo)) {
                return this.save(this.infos);
            }
        } catch (Exception e) {
            StaticLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public synchronized Paging<ZKInfo> getPage(int limit, Map<String, Object> params) {
        // 加载数据
        List<ZKInfo> infos = this.load();
        // 分页对象
        Paging<ZKInfo> paging = new Paging<>(infos, limit);
        // 数据为空
        if (CollUtil.isNotEmpty(infos)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StrUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                infos = infos.parallelStream().filter(z ->
                        z.getHost() != null && z.getHost().contains(kw)
                                || z.getName() != null && z.getName().toLowerCase().contains(kw)
                                || z.getRemark() != null && z.getRemark().toLowerCase().contains(kw)
                ).collect(Collectors.toList());
            }
            // 对数据排序
            infos = infos.parallelStream().sorted().collect(Collectors.toList());
            // 添加到分页数据
            paging.dataList(infos);
        }
        return paging;
    }
}
