package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.util.FileStore;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSearchHistory;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class ZKSearchHistoryStore extends FileStore<ZKSearchHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final ZKSearchHistoryStore INSTANCE = new ZKSearchHistoryStore();

    {
        this.filePath(ZKConst.STORE_PATH + "zk_search_history.json");
        log.info("ZKSearchHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<ZKSearchHistory> load() {
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(text, ZKSearchHistory.class);
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
            if (history.compare(CollUtil.getLast(hisList))) {
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
            log.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    /**
     * 添加搜索历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addSearchHistory(@NonNull String kw) {
        return this.add(new ZKSearchHistory(kw, 1));
    }

    /**
     * 添加替换历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addReplaceHistory(@NonNull String kw) {
        return this.add(new ZKSearchHistory(kw, 2));
    }

    @Override
    public synchronized boolean update(@NonNull ZKSearchHistory history) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(@NonNull ZKSearchHistory history) {
        try {
            List<ZKSearchHistory> histories = this.load();
            if (histories.removeIf(h -> h.compare(history))) {
                return this.save(histories);
            }
        } catch (Exception e) {
            log.warn("delete error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public Paging<ZKSearchHistory> getPage(int limit, Map<String, Object> params) {
        throw new UnsupportedOperationException();
    }
}
