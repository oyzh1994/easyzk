package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKCollect;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSSHInfo;
import cn.oyzh.fx.common.jdbc.JdbcStore;
import cn.oyzh.fx.common.util.CollectionUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/09/26
 */
public class ZKInfoStore2 extends JdbcStore<ZKInfo> {

    /**
     * 当前实例
     */
    public static final ZKInfoStore2 INSTANCE = new ZKInfoStore2();

    public List<ZKInfo> load() {
        List<ZKInfo> list = super.selectList();
        for (ZKInfo info : list) {
            // 处理ssh
            info.setSshInfo(ZKSSHInfoStore.INSTANCE.find(info.getId()));
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
