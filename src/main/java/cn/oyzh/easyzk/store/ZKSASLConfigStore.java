package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

/**
 * sasl配置
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKSASLConfigStore extends JdbcStandardStore<ZKSASLConfig> {

    /**
     * 当前实例
     */
    public static final ZKSASLConfigStore INSTANCE = new ZKSASLConfigStore();

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
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

    /**
     * 根据zk连接id获取配置
     *
     * @param iid zk连接id
     * @return sasl配置
     */
    public ZKSASLConfig getByIid(String iid) {
        return super.selectOne(QueryParam.of("iid", iid));
    }
}
