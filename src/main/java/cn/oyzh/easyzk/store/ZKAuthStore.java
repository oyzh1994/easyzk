package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;
import cn.oyzh.store.jdbc.SelectParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk认证存储
 *
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKAuthStore extends JdbcStandardStore<ZKAuth> {

    /**
     * 当前实例
     */
    public static final ZKAuthStore INSTANCE = new ZKAuthStore();

    /**
     * 加载数据列表
     *
     * @param iid zk连接id
     * @return 数据列表
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public List<ZKAuth> load(String iid) {
        return super.selectList(QueryParam.of("iid", iid));
    }

    /**
     * 加载已启用的数据列表
     *
     * @param iid zk连接id
     * @return 已启用的数据列表
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
    public List<ZKAuth> loadEnable(String iid) {
        if (StringUtil.isEmpty(iid)) {
            return Collections.emptyList();
        }
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

    /**
     * 是否存在认证
     *
     * @param user     用户名
     * @param password 密码
     * @param iid      zk连接id
     * @return 结果
     * @see cn.oyzh.easyzk.domain.ZKConnect
     */
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
    protected Class<ZKAuth> modelClass() {
        return ZKAuth.class;
    }
}
