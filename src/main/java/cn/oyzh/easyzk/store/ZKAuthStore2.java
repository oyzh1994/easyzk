package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.jdbc.JdbcStore;
import cn.oyzh.fx.common.jdbc.PageParam;
import cn.oyzh.fx.common.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKAuthStore2 extends JdbcStore<ZKAuth> {

    /**
     * 当前实例
     */
    public static final ZKAuthStore2 INSTANCE = new ZKAuthStore2();

    public List<ZKAuth> load() {
        return super.selectList();
    }

    public boolean replace(ZKAuth model) {
        boolean result = false;
        if (model != null) {
            if (this.exist(model.getUser(), model.getPassword())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }
        }
        return result;
    }

    public boolean delete(String user, String password) {
        if (StringUtil.isEmpty(user) || StringUtil.isEmpty(password)) {

            Map<String, Object> params = new HashMap<>();
            params.put("user", user);
            params.put("password", password);
            return this.delete(params);
        }
        return false;
    }

    public Paging<ZKAuth> getPage(long pageNo, int limit, String kw) {
        PageParam pageParam = new PageParam(limit, pageNo * limit);
        List<ZKAuth> list = this.selectPage(kw, List.of("user", "password"), pageParam);
        Paging<ZKAuth> paging;
        if (CollUtil.isNotEmpty(list)) {
            long count = this.selectCount(kw, List.of("kw"));
            paging = new Paging<>(list, limit, count);
            paging.currentPage(pageNo);
        } else {
            paging = new Paging<>(limit);
        }
        return paging;
    }

    public boolean exist(String user, String password) {
        if (StringUtil.isNotBlank(user) && StringUtil.isNotBlank(password)) {
            Map<String, Object> params = new HashMap<>();
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
