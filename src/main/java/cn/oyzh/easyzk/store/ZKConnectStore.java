package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKCollect;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.domain.ZKSSHConfig;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.List;

/**
 * zk连接存储
 *
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKConnectStore extends JdbcStandardStore<ZKConnect> {

    /**
     * 当前实例
     */
    public static final ZKConnectStore INSTANCE = new ZKConnectStore();

    /**
     * 认证存储
     */
    private final ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    /**
     * 过滤存储
     */
    private final ZKFilterStore filterStore = ZKFilterStore.INSTANCE;

    /**
     * 收藏存储
     */
    private final ZKCollectStore collectStore = ZKCollectStore.INSTANCE;

    /**
     * ssh配置存储
     */
    private final ZKSSHConfigStore sshConfigStore = ZKSSHConfigStore.INSTANCE;

    /**
     * sasl配置存储
     */
    private final ZKSASLConfigStore saslConfigStore = ZKSASLConfigStore.INSTANCE;

    /**
     * 加载列表
     *
     * @return zk连接列表
     */
    public List<ZKConnect> load() {
        return super.selectList();
    }

    /**
     * 加载列表，完整信息，给导出用
     *
     * @return zk连接列表
     */
    public List<ZKConnect> loadFull() {
        List<ZKConnect> connects = super.selectList();
        for (ZKConnect connect : connects) {
            connect.setAuths(this.authStore.loadByIid(connect.getId()));
            connect.setFilters(this.filterStore.loadByIid(connect.getId()));
            connect.setCollects(this.collectStore.listByIid(connect.getId()));
            connect.setSshConfig(this.sshConfigStore.getByIid(connect.getId()));
            connect.setSaslConfig(this.saslConfigStore.getByIid(connect.getId()));
        }
        return connects;
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
                this.sshConfigStore.replace(sshConfig);
            } else {
                this.sshConfigStore.deleteByIid(model.getId());
            }

            // sasl处理
            ZKSASLConfig saslConfig = model.getSaslConfig();
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("iid", model.getId()));
            if (saslConfig != null) {
                saslConfig.setIid(model.getId());
                this.saslConfigStore.replace(saslConfig);
            } else {
                this.saslConfigStore.delete(param);
            }

            // 收藏处理
            List<ZKCollect> collects = model.getCollects();
            if (CollectionUtil.isNotEmpty(collects)) {
                this.collectStore.deleteByIid(model.getId());
                for (ZKCollect collect : collects) {
                    this.collectStore.replace(collect);
                }
            }

            // 认证处理
            List<ZKAuth> auths = model.getAuths();
            if (CollectionUtil.isNotEmpty(auths)) {
                this.authStore.deleteByIid(model.getId());
                for (ZKAuth auth : auths) {
                    auth.setIid(model.getId());
                    this.authStore.replace(auth);
                }
            }

            // 过滤处理
            List<ZKFilter> filters = model.getFilters();
            if (CollectionUtil.isNotEmpty(filters)) {
                this.filterStore.deleteByIid(model.getId());
                for (ZKFilter filter : filters) {
                    filter.setIid(model.getId());
                    this.filterStore.replace(filter);
                }
            }
        }
        return result;
    }

    @Override
    public boolean delete(ZKConnect model) {
        boolean result = super.delete(model);
        // 删除关联配置
        if (result) {
            this.authStore.deleteByIid(model.getId());
            this.filterStore.deleteByIid(model.getId());
            this.collectStore.deleteByIid(model.getId());
            this.sshConfigStore.deleteByIid(model.getId());
            this.saslConfigStore.deleteByIid(model.getId());
        }
        return result;
    }

    @Override
    protected Class<ZKConnect> modelClass() {
        return ZKConnect.class;
    }
}
