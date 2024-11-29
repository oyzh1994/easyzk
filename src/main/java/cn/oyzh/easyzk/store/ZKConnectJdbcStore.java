package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKConnectJdbcStore extends JdbcStore<ZKConnect> {

    /**
     * 当前实例
     */
    public static final ZKConnectJdbcStore INSTANCE = new ZKConnectJdbcStore();

    public List<ZKConnect> load() {
        List<ZKConnect> list = super.selectList();
        // 处理ssh信息
        for (ZKConnect info : list) {
            info.setSshConnect(ZKSSHConnectJdbcStore.INSTANCE.find(info.getId()));
        }
        return list;
    }

    public boolean replace(ZKConnect info) {
        boolean result = false;
        if (info != null) {
            if (super.exist(info.getId())) {
                result = this.update(info);
            } else {
                result = this.insert(info);
            }

            // ssh信息处理
            ZKSSHConnect connect = info.getSshConnect();
            if (info.getSshConnect() != null) {
                ZKSSHConnectJdbcStore.INSTANCE.replace(connect);
            } else {
                DeleteParam param = new DeleteParam();
                param.addQueryParam(new QueryParam("iid", info.getId()));
                ZKSSHConnectJdbcStore.INSTANCE.delete(connect);
            }

            // 收藏处理
            List<String> collects = info.getCollects();
            if (CollectionUtil.isNotEmpty(collects)) {
                for (String collect : collects) {
                    ZKCollectJdbcStore.INSTANCE.replace(info.getId(), collect);
                }
            } else {
                ZKCollectJdbcStore.INSTANCE.delete(info.getId());
            }
        }
        return result;
    }

    @Override
    protected ZKConnect newModel() {
        return new ZKConnect();
    }

    @Override
    protected Class<ZKConnect> modelClass() {
        return ZKConnect.class;
    }
}
