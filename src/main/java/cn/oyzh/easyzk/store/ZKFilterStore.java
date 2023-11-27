package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * zk过滤存储
 *
 * @author oyzh
 * @since 2022/12/16
 */
//@Slf4j
public class ZKFilterStore extends ArrayFileStore<ZKFilter> {

    /**
     * 当前实例
     */
    public static final ZKFilterStore INSTANCE = new ZKFilterStore();

    {
        this.filePath(ZKConst.STORE_PATH + "zk_filter.json");
        StaticLog.info("ZKFilterStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<ZKFilter> load() {
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        List<ZKFilter> filters = JSONUtil.toList(text, ZKFilter.class);
        if (CollUtil.isNotEmpty(filters)) {
            filters = filters.parallelStream().filter(Objects::nonNull).sorted((o1, o2) -> o1.getKw().compareToIgnoreCase(o2.getKw())).collect(Collectors.toList());
        }
        return filters;
    }

    /**
     * 加载已启用的数据列表
     *
     * @return 已启用的数据列表
     */
    public synchronized List<ZKFilter> loadEnable() {
        List<ZKFilter> filters = this.load();
        if (CollUtil.isNotEmpty(filters)) {
            filters = filters.parallelStream().filter(ZKFilter::isEnable).toList();
        }
        return filters;
    }

    @Override
    public synchronized boolean add(@NonNull ZKFilter filter) {
        try {
            List<ZKFilter> filters = this.load();
            Optional<ZKFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isEmpty()) {
                // 添加到集合
                filters.add(filter);
                // 更新数据
                return this.save(filters);
            }
            return true;
        } catch (Exception e) {
            StaticLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKFilter filter) {
        try {
            List<ZKFilter> filters = this.load();
            Optional<ZKFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isPresent()) {
                optional.get().copy(filter);
                // 更新数据
                return this.save(filters);
            }
        } catch (Exception e) {
            StaticLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKFilter filter) {
        try {
            List<ZKFilter> filters = this.load();
            if (CollUtil.isEmpty(filters)) {
                return false;
            }
            Optional<ZKFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isPresent()) {
                // 移除zk信息
                filters.remove(optional.get());
                // 更新数据
                return this.save(filters);
            }
        } catch (Exception e) {
            StaticLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public synchronized Paging<ZKFilter> getPage(int limit, Map<String, Object> params) {
        // 加载数据
        List<ZKFilter> list = this.load();
        // 分页对象
        Paging<ZKFilter> paging = new Paging<>(list, limit);
        // 数据为空
        if (CollUtil.isNotEmpty(list)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StrUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                list = list.parallelStream().filter(z -> z.getKw().contains(kw)).collect(Collectors.toList());
            }
            // 添加到分页数据
            paging.dataList(list);
        }
        return paging;
    }

    public synchronized boolean exist(String kw) {
        List<ZKFilter> filters = this.load();
        if (CollUtil.isEmpty(filters)) {
            return false;
        }
        Optional<ZKFilter> optional = filters.parallelStream().filter(f -> f.compare(kw)).findFirst();
        return optional.isPresent();
    }
}
