package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;
import cn.oyzh.store.jdbc.SelectParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKFilterStore extends JdbcStandardStore<ZKFilter> {

    /**
     * 当前实例
     */
    public static final ZKFilterStore INSTANCE = new ZKFilterStore();

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

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
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
    protected Class<ZKFilter> modelClass() {
        return ZKFilter.class;
    }
}
