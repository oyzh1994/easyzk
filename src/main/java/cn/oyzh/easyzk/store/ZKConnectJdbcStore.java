package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.domain.ZKSSHConfig;
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
            info.setSshConfig(ZKSSHConfigJdbcStore.INSTANCE.find(info.getId()));
        }
        return list;
    }

    public boolean replace(ZKConnect zkConnect) {
        boolean result = false;
        if (zkConnect != null) {
            if (super.exist(zkConnect.getId())) {
                result = this.update(zkConnect);
            } else {
                result = this.insert(zkConnect);
            }

            // ssh处理
            ZKSSHConfig sshConfig = zkConnect.getSshConfig();
            if (sshConfig != null) {
                sshConfig.setIid(zkConnect.getId());
                ZKSSHConfigJdbcStore.INSTANCE.replace(sshConfig);
            } else {
                DeleteParam param = new DeleteParam();
                param.addQueryParam(new QueryParam("iid", zkConnect.getId()));
                ZKSSHConfigJdbcStore.INSTANCE.delete(param);
            }

            // sasl处理
            ZKSASLConfig saslConfig = zkConnect.getSaslConfig();
                DeleteParam param = new DeleteParam();
                param.addQueryParam(new QueryParam("iid", zkConnect.getId()));
                ZKSASLConfigJdbcStore.INSTANCE.delete(param);
            if (saslConfig != null) {
                saslConfig.setIid(zkConnect.getId());
                ZKSASLConfigJdbcStore.INSTANCE.replace(saslConfig);
            } else {
            }

            // 收藏处理
            List<String> collects = zkConnect.getCollects();
            if (CollectionUtil.isNotEmpty(collects)) {
                for (String collect : collects) {
                    ZKCollectJdbcStore.INSTANCE.replace(zkConnect.getId(), collect);
                }
            } else {
                ZKCollectJdbcStore.INSTANCE.deleteByIid(zkConnect.getId());
            }

            // 认证处理
            ZKAuthJdbcStore.INSTANCE.deleteByIid(zkConnect.getId());
            List<ZKAuth> auths = zkConnect.getAuths();
            if (CollectionUtil.isNotEmpty(auths)) {
                for (ZKAuth auth : auths) {
                    auth.setIid(zkConnect.getId());
                    ZKAuthJdbcStore.INSTANCE.replace(auth);
                }
            }

            // 过滤处理
            ZKFilterJdbcStore.INSTANCE.deleteByIid(zkConnect.getId());
            List<ZKFilter> filters = zkConnect.getFilters();
            if (CollectionUtil.isNotEmpty(filters)) {
                for (ZKFilter filter : filters) {
                    filter.setIid(zkConnect.getId());
                    ZKFilterJdbcStore.INSTANCE.replace(filter);
                }
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
