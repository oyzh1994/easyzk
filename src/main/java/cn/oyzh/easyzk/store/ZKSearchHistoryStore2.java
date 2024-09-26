package cn.oyzh.easyzk.store;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.domain.ZKSearchHistory;
import cn.oyzh.fx.common.sqlite.QueryParam;
import cn.oyzh.fx.common.sqlite.SqliteStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * zk搜索历史存储
 *
 * @author oyzh
 * @since 2022/12/16
 */
//@Slf4j
public class ZKSearchHistoryStore2 extends SqliteStore<ZKSearchHistory> {

    /**
     * 最大历史数量
     */
    public static int His_Max_Size = 50;

    /**
     * 当前实例
     */
    public static final ZKSearchHistoryStore2 INSTANCE = new ZKSearchHistoryStore2();

    public List<String> listKw(byte type) {
        // 查询总数
        QueryParam queryParam = new QueryParam("type", type);
        List<ZKSearchHistory> list = super.selectList(queryParam);
        return list.parallelStream().map(ZKSearchHistory::getKw).collect(Collectors.toList());
    }

    /**
     * 添加历史
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(ZKSearchHistory model) {
        String kw = model.getKw();
        byte type = model.getType();
        if (StrUtil.isNotBlank(kw)) {
            Map<String, Object> params = new HashMap<>();
            params.put("kw", kw);
            params.put("type", type);
            // 删除旧数据
            if (super.exist(params)) {
                super.delete(params);
            }
            // 新增数据
            boolean result = super.insert(model);
            // 查询总数
            List<QueryParam> queryParams = new ArrayList<>();
            queryParams.add(new QueryParam("type", type));
            long count = super.selectCount(queryParams);
            // 删除超过部分数据
            if (count > His_Max_Size) {
                super.delete(params, count - His_Max_Size);
            }
            return result;
        }
        return false;
    }

    /**
     * 添加搜索历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addSearchKw(String kw) {
        return this.replace(new ZKSearchHistory(kw, (byte) 1));
    }

    /**
     * 添加替换历史
     *
     * @param kw 关键词
     * @return 结果
     */
    public boolean addReplaceKw(String kw) {
        return this.replace(new ZKSearchHistory(kw, (byte) 2));
    }


    @Override
    protected ZKSearchHistory newModel() {
        return new ZKSearchHistory();
    }

    @Override
    protected Class<ZKSearchHistory> modelClass() {
        return ZKSearchHistory.class;
    }
}
