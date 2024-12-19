package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;
import cn.oyzh.store.jdbc.SelectParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKFilterJdbcStore extends JdbcStore<ZKFilter> {

    /**
     * 当前实例
     */
    public static final ZKFilterJdbcStore INSTANCE = new ZKFilterJdbcStore();

    // /**
    //  * 加载已启用的数据列表
    //  *
    //  * @param iid zk连接id
    //  * @return 数据列表
    //  * @see cn.oyzh.easyzk.domain.ZKConnect
    //  */
    // public List<ZKFilter> load(String iid) {
    //     return super.selectList(QueryParam.of("iid", iid));
    // }

    /**
     * 加载已启用的数据列表
     *
     * @param iid zk连接id
     * @return 已启用的数据列表
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public List<ZKFilter> loadEnable(String iid) {
        SelectParam selectParam = new SelectParam();
        selectParam.addQueryParam(QueryParam.of("enable", 1));
        selectParam.addQueryParam(QueryParam.of("iid", iid));
        return super.selectList(selectParam);
    }

    public boolean replace(ZKFilter model) {
        boolean result = false;
        if (model != null) {
            if (this.exist(model.getKw(), model.getIid())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }
        }
        return result;
    }

    // public boolean delete(String kw) {
    //     if (StringUtil.isNotBlank(kw)) {
    //         DeleteParam param = new DeleteParam();
    //         param.addQueryParam(new QueryParam("kw", kw));
    //         return this.delete(param);
    //     }
    //     return false;
    // }

    /**
     * 根据iid删除数据
     *
     * @param iid zk连接id
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public boolean deleteByIid(String iid) {
        if (StringUtil.isNotBlank(iid)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("iid", iid));
            return this.delete(param);
        }
        return false;
    }

    // public Paging<ZKFilter> getPage(long pageNo, int limit, String kw, String iid) {
    //     QueryParam queryParam = new QueryParam("iid", iid);
    //     PageParam pageParam = new PageParam(limit, pageNo * limit);
    //     pageParam.addQueryParam(queryParam);
    //     List<ZKFilter> list = this.selectPage(kw, List.of("kw"), pageParam);
    //     Paging<ZKFilter> paging;
    //     if (CollectionUtil.isNotEmpty(list)) {
    //         long count = this.selectCount(kw, List.of("kw"), QueryParams.of(queryParam));
    //         paging = new Paging<>(list, limit, count);
    //         paging.currentPage(pageNo);
    //     } else {
    //         paging = new Paging<>(limit);
    //     }
    //     return paging;
    // }

    /**
     * 判断是否存在
     *
     * @param kw  关键字
     * @param iid zk连接id
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public boolean exist(String kw, String iid) {
        if (StringUtil.isNotBlank(kw)) {
            Map<String, Object> params = new HashMap<>();
            params.put("kw", kw);
            params.put("iid", iid);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected ZKFilter newModel() {
        return new ZKFilter();
    }

    @Override
    protected Class<ZKFilter> modelClass() {
        return ZKFilter.class;
    }

    // public Paging<ZKFilter> getPage(long pageNo, int i, String text) {
    //     return null;
    // }
}
