package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.domain.ZKSSHConfig;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKConnectStore extends JdbcStandardStore<ZKConnect> {

    /**
     * 当前实例
     */
    public static final ZKConnectStore INSTANCE = new ZKConnectStore();

    /**
     * 加载列表
     *
     * @return zk连接列表
     */
    public List<ZKConnect> load() {
        return super.selectList();
    }

    /**
     * 替换
     *
     * @param model 模型
     * @return 结果
     */
    public boolean replace(ZKConnect model) {
        boolean result = false;
        if (model != null) {
            if (super.exist(model.getId())) {
                result = this.update(model);
            } else {
                result = this.insert(model);
            }

            // ssh处理
            ZKSSHConfig sshConfig = model.getSshConfig();
            if (sshConfig != null) {
                sshConfig.setIid(model.getId());
                ZKSSHConfigStore.INSTANCE.replace(sshConfig);
            } else {
                DeleteParam param = new DeleteParam();
                param.addQueryParam(new QueryParam("iid", model.getId()));
                ZKSSHConfigStore.INSTANCE.delete(param);
            }

            // sasl处理
            ZKSASLConfig saslConfig = model.getSaslConfig();
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("iid", model.getId()));
            if (saslConfig != null) {
                saslConfig.setIid(model.getId());
                ZKSASLConfigStore.INSTANCE.replace(saslConfig);
            } else {
                ZKSASLConfigStore.INSTANCE.delete(param);
            }

            // 收藏处理
            List<String> collects = model.getCollects();
            if (CollectionUtil.isNotEmpty(collects)) {
                for (String collect : collects) {
                    ZKCollectStore.INSTANCE.replace(model.getId(), collect);
                }
                // } else {
                //     ZKCollectStore.INSTANCE.deleteByIid(model.getId());
            }

            // 认证处理
            ZKAuthStore.INSTANCE.deleteByIid(model.getId());
            List<ZKAuth> auths = model.getAuths();
            if (CollectionUtil.isNotEmpty(auths)) {
                for (ZKAuth auth : auths) {
                    auth.setIid(model.getId());
                    ZKAuthStore.INSTANCE.replace(auth);
                }
            } else {
                ZKAuthStore.INSTANCE.deleteByIid(model.getId());
            }

            // 过滤处理
            ZKFilterStore.INSTANCE.deleteByIid(model.getId());
            List<ZKFilter> filters = model.getFilters();
            if (CollectionUtil.isNotEmpty(filters)) {
                for (ZKFilter filter : filters) {
                    filter.setIid(model.getId());
                    ZKFilterStore.INSTANCE.replace(filter);
                }
            } else {
                ZKFilterStore.INSTANCE.deleteByIid(model.getId());
            }
        }
        return result;
    }

    @Override
    protected Class<ZKConnect> modelClass() {
        return ZKConnect.class;
    }
}
