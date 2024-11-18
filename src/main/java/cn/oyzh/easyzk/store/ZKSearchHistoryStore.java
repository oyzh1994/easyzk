package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSearchHistory;
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
import java.util.stream.Collectors;

/**
 * zk搜索历史存储
 *
 * @author oyzh
 * @since 2022/12/16
 */
//@Slf4j
@Deprecated
public class ZKSearchHistoryStore extends ArrayFileStore<ZKSearchHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final ZKSearchHistoryStore INSTANCE = new ZKSearchHistoryStore();

    // {
    //     this.filePath(ZKConst.STORE_PATH + "zk_search_history.json");
    //     JulLog.info("ZKSearchHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    // }

    public ZKSearchHistoryStore() {
        this.filePath(ZKConst.STORE_PATH + "zk_search_history.json");
    }

    @Override
    public synchronized List<ZKSearchHistory> load() {
        // 从文件中读取字符串内容
        String text = FileUtil.readString(this.storeFile(), this.charset());
        // 如果字符串为空，则返回空列表
        if (StringUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        // 将字符串解析为搜索历史记录列表
        return JSONUtil.toBeanList(text, ZKSearchHistory.class);
    }

    /**
     * 获取搜索词
     *
     * @return 搜索词列表
     */
    public synchronized List<String> getSearchKw() {
        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), 1)).map(ZKSearchHistory::getKw).collect(Collectors.toList());
    }

    /**
     * 获取替换词
     *
     * @return 替换词列表
     */
    public synchronized List<String> getReplaceKw() {
        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), 2)).map(ZKSearchHistory::getKw).collect(Collectors.toList());
    }

    @Override
    public synchronized boolean add(@NonNull ZKSearchHistory history) {
        try {
            // 历史列表
            List<ZKSearchHistory> histories = this.load();
            // 过滤出当前类型
            List<ZKSearchHistory> hisList = histories.parallelStream().filter(h -> Objects.equals(h.getType(), history.getType())).collect(Collectors.toList());
            // 最新的数据是当前数据，则无需添加
            if (history.compare(CollectionUtil.getLast(hisList))) {
                return true;
            }
            // 移除当前添加内容
            histories.removeIf(h -> h.compare(history));
            // 添加到集合
            histories.add(history);
            // 对超出限制的数据，进行删除
            int limit = hisList.size() - His_Max_Size + 1;
            if (limit > 0) {
                List<ZKSearchHistory> delList = hisList.parallelStream().limit(limit).toList();
                histories.removeAll(delList);
            }
            // 保存数据
            return this.save(histories);
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 添加搜索历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public synchronized boolean addSearchHistory(@NonNull String kw) {
        return this.add(new ZKSearchHistory(kw, (byte) 1));
    }

    /**
     * 添加替换历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public synchronized boolean addReplaceHistory(@NonNull String kw) {
        return this.add(new ZKSearchHistory(kw, (byte) 2));
    }

    @Override
    public Paging<ZKSearchHistory> getPage(int limit, Map<String, Object> params) {
        return super.getPage(limit, params);
    }
}
