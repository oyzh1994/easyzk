package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKAuthJdbcStore extends JdbcStore<ZKAuth> {

    /**
     * 当前实例
     */
    public static final ZKAuthJdbcStore INSTANCE = new ZKAuthJdbcStore();

    public List<ZKAuth> load() {
        return super.selectList();
    }

    public boolean replace(ZKAuth model) {
        boolean result = false;
        if (model != null) {
            if (this.exist(model.getUser(), model.getPassword(), model.getIid())) {
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

    // public boolean delete(String user, String password, String iid) {
    //     if (StringUtil.isEmpty(user) || StringUtil.isEmpty(password)) {
    //         Map<String, Object> params = new HashMap<>();
    //         params.put("iid", iid);
    //         params.put("user", user);
    //         params.put("password", password);
    //         return this.delete(params);
    //     }
    //     return false;
    // }

    // public Paging<ZKAuth> getPage(long pageNo, int limit, String kw) {
    //     PageParam pageParam = new PageParam(limit, pageNo * limit);
    //     List<ZKAuth> list = this.selectPage(kw, List.of("user", "password"), pageParam);
    //     Paging<ZKAuth> paging;
    //     if (CollectionUtil.isNotEmpty(list)) {
    //         long count = this.selectCount(kw, List.of("kw"));
    //         paging = new Paging<>(list, limit, count);
    //         paging.currentPage(pageNo);
    //     } else {
    //         paging = new Paging<>(limit);
    //     }
    //     return paging;
    // }

    public boolean exist(String user, String password, String iid) {
        if (StringUtil.isNotBlank(user) && StringUtil.isNotBlank(password) && StringUtil.isNotBlank(iid)) {
            Map<String, Object> params = new HashMap<>();
            params.put("iid", iid);
            params.put("user", user);
            params.put("password", password);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected ZKAuth newModel() {
        return new ZKAuth();
    }

    @Override
    protected Class<ZKAuth> modelClass() {
        return ZKAuth.class;
    }
}
