package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.HashMap;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSSHInfoStore extends JdbcStore<ZKSSHConnect> {

    /**
     * 当前实例
     */
    public static final ZKSSHInfoStore INSTANCE = new ZKSSHInfoStore();

    public ZKSSHConnect find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(ZKSSHConnect model) {
        Map<String, Object> params = new HashMap<>();
        params.put("iid", model.getIid());
        super.delete(params);
        return this.insert(model);
    }

    @Override
    protected ZKSSHConnect newModel() {
        return new ZKSSHConnect();
    }

    @Override
    protected Class<ZKSSHConnect> modelClass() {
        return ZKSSHConnect.class;
    }
}
