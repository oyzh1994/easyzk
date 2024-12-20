package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSSHConfig;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSSHConfigJdbcStore extends JdbcStore<ZKSSHConfig> {

    /**
     * 当前实例
     */
    public static final ZKSSHConfigJdbcStore INSTANCE = new ZKSSHConfigJdbcStore();

    public ZKSSHConfig find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(ZKSSHConfig model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected ZKSSHConfig newModel() {
        return new ZKSSHConfig();
    }

    @Override
    protected Class<ZKSSHConfig> modelClass() {
        return ZKSSHConfig.class;
    }
}
