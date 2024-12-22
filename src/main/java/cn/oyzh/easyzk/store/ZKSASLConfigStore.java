package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSASLConfigStore extends JdbcStandardStore<ZKSASLConfig> {

    /**
     * 当前实例
     */
    public static final ZKSASLConfigStore INSTANCE = new ZKSASLConfigStore();

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
    protected Class<ZKSASLConfig> modelClass() {
        return ZKSASLConfig.class;
    }

    public ZKSASLConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}
