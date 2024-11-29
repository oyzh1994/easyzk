package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSSHConnectJdbcStore extends JdbcStore<ZKSSHConnect> {

    /**
     * 当前实例
     */
    public static final ZKSSHConnectJdbcStore INSTANCE = new ZKSSHConnectJdbcStore();

    public ZKSSHConnect find(String iid) {
        QueryParam param = new QueryParam();
        param.setName("iid");
        param.setData(iid);
        return super.selectOne(param);
    }

    public boolean replace(ZKSSHConnect model) {
        String iid = model.getIid();
        if (super.exist(iid)) {
            super.update(model);
        }
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
