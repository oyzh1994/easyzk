package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.common.dto.Paging;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.FileUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.store.json.ArrayFileStore;
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
@Deprecated
public class ZKFilterStore extends ArrayFileStore<ZKFilter> {

    /**
     * 当前实例
     */
    public static final ZKFilterStore INSTANCE = new ZKFilterStore();

    // {
    //     this.filePath(ZKConst.STORE_PATH + "zk_filter.json");
    //     JulLog.info("ZKFilterStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    // }

    public ZKFilterStore() {
        this.filePath(ZKConst.STORE_PATH + "zk_filter.json");
    }

    @Override
    public synchronized List<ZKFilter> load() {
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StringUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        List<ZKFilter> filters = JSONUtil.toList(text, ZKFilter.class);
        if (CollectionUtil.isNotEmpty(filters)) {
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
        // 加载所有数据
        List<ZKFilter> filters = this.load();
        // 如果数据列表不为空
        if (CollectionUtil.isNotEmpty(filters)) {
            // 过滤出已启用的数据
            filters = filters.parallelStream().filter(ZKFilter::isEnable).toList();
        }
        // 返回已启用的数据列表
        return filters;
    }

    @Override
    public synchronized boolean add(@NonNull ZKFilter filter) {
        try {
            // 加载过滤器集合
            List<ZKFilter> filters = this.load();
            // 并行地在过滤器集合中筛选符合给定过滤器条件的过滤器
            Optional<ZKFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isEmpty()) {
                // 如果没有找到符合要求的过滤器，则将新的过滤器添加到集合中
                filters.add(filter);
                // 更新数据并保存过滤器集合
                return this.save(filters);
            }
            return true;
        } catch (Exception e) {
            // 捕获异常并打印错误日志
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull ZKFilter filter) {
        try {
            // 加载过滤器列表
            List<ZKFilter> filters = this.load();
            // 并行地在过滤器列表中进行比较，找到匹配的过滤器
            Optional<ZKFilter> optional = filters.parallelStream().filter(filter::compare).findFirst();
            if (optional.isPresent()) {
                // 复制过滤器
                optional.get().copy(filter);
                // 更新数据
                return this.save(filters);
            }
        } catch (Exception e) {
            JulLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull ZKFilter filter) {
        try {
            List<ZKFilter> filters = this.load();
            if (CollectionUtil.isEmpty(filters)) {
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
            JulLog.warn("delete error,err:{}", e.getMessage());
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
        if (CollectionUtil.isNotEmpty(list)) {
            String searchKeyWord = params == null ? null : (String) params.get("searchKeyWord");
            // 过滤数据
            if (StringUtil.isNotBlank(searchKeyWord)) {
                final String kw = searchKeyWord.toLowerCase().trim();
                list = list.parallelStream().filter(z -> z.getKw().contains(kw)).collect(Collectors.toList());
            }
            // 添加到分页数据
            paging.dataList(list);
        }
        return paging;
    }

    /**
     * 判断给定关键字是否存在于过滤器列表中
     *
     * @param kw 给定的关键字
     * @return 若存在则返回true，否则返回false
     */
    public synchronized boolean exist(String kw) {
        List<ZKFilter> filters = this.load();
        if (CollectionUtil.isEmpty(filters)) {
            return false;
        }
        Optional<ZKFilter> optional = filters.parallelStream().filter(f -> f.compare(kw)).findFirst();
        return optional.isPresent();
    }
}
