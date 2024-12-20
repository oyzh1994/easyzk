package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSASLConfigJdbcStore extends JdbcStore<ZKSASLConfig> {

    /**
     * 当前实例
     */
    public static final ZKSASLConfigJdbcStore INSTANCE = new ZKSASLConfigJdbcStore();

    public ZKSASLConfig find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(ZKSASLConfig model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            return super.update(model);
        }
        return this.insert(model);
    }

    @Override
    protected ZKSASLConfig newModel() {
        return new ZKSASLConfig();
    }

    @Override
    protected Class<ZKSASLConfig> modelClass() {
        return ZKSASLConfig.class;
    }
}
