package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKInfoJdbcStore extends JdbcStore<ZKInfo> {

    /**
     * 当前实例
     */
    public static final ZKInfoJdbcStore INSTANCE = new ZKInfoJdbcStore();

    public List<ZKInfo> load() {
        List<ZKInfo> list = super.selectList();
        // 处理ssh
        for (ZKInfo info : list) {
            info.setSshConnect(ZKSSHInfoStore.INSTANCE.find(info.getId()));
        }
        return list;
    }

    public boolean replace(ZKInfo info) {
        boolean result = false;
        if (info != null) {
            if (super.exist(info.getId())) {
                result = this.update(info);
            } else {
                result = this.insert(info);
            }
            // ssh处理
            ZKSSHConnect connect = info.getSshConnect();
            if (info.getSshConnect() != null) {
                ZKSSHInfoStore.INSTANCE.replace(connect);
            } else {
                DeleteParam param = new DeleteParam();
                param.addQueryParam(new QueryParam("iid", info.getId()));
                ZKSSHInfoStore.INSTANCE.delete(connect);
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
    protected ZKInfo newModel() {
        return new ZKInfo();
    }

    @Override
    protected Class<ZKInfo> modelClass() {
        return ZKInfo.class;
    }
}
