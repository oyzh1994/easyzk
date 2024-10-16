package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.jdbc.DeleteParam;
import cn.oyzh.fx.common.jdbc.JdbcStore;
import cn.oyzh.fx.common.jdbc.PageParam;
import cn.oyzh.fx.common.jdbc.QueryParam;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.common.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKFilterStore2 extends JdbcStore<ZKFilter> {

    /**
     * 当前实例
     */
    public static final ZKFilterStore2 INSTANCE = new ZKFilterStore2();

    public List<ZKFilter> load() {
        return super.selectList();
    }

    /**
     * 加载已启用的数据列表
     *
     * @return 已启用的数据列表
     */
    public List<ZKFilter> loadEnable() {
        QueryParam queryParam = new QueryParam("enable", 1);
        return super.selectList(queryParam);
    }

    public boolean replace(ZKFilter model) {
        boolean result = false;
        if (model != null) {
            if (this.exist(model.getKw())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }
        }
        return result;
    }

    public boolean delete(String kw) {
        if (StringUtil.isNotBlank(kw)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("kw", kw));
            return this.delete(param);
        }
        return false;
    }

    public Paging<ZKFilter> getPage(long pageNo, int limit, String kw) {
        PageParam pageParam = new PageParam(limit, pageNo * limit);
        List<ZKFilter> list = this.selectPage(kw, List.of("kw"), pageParam);
        Paging<ZKFilter> paging;
        if (CollectionUtil.isNotEmpty(list)) {
            long count = this.selectCount(kw, List.of("kw"));
            paging = new Paging<>(list, limit, count);
            paging.currentPage(pageNo);
        } else {
            paging = new Paging<>(limit);
        }
        return paging;
    }

    public boolean exist(String kw) {
        if (StringUtil.isNotBlank(kw)) {
            Map<String, Object> params = new HashMap<>();
            params.put("kw", kw);
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
}
