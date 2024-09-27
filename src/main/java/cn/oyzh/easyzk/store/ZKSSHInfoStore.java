package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSSHInfo;
import cn.oyzh.fx.common.jdbc.QueryParam;
import cn.oyzh.fx.common.sqlite.SqliteStore;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSSHInfoStore extends SqliteStore<ZKSSHInfo> {

    /**
     * 当前实例
     */
    public static final ZKSSHInfoStore INSTANCE = new ZKSSHInfoStore();

    public ZKSSHInfo find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(ZKSSHInfo model) {
        Map<String, Object> params = new HashMap<>();
        params.put("iid", model.getIid());
        super.delete(params);
        return this.insert(model);
    }

    @Override
    protected ZKSSHInfo newModel() {
        return new ZKSSHInfo();
    }

    @Override
    protected Class<ZKSSHInfo> modelClass() {
        return ZKSSHInfo.class;
    }
}
