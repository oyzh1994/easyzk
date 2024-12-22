package cn.oyzh.easyzk.store;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.store.jdbc.DeleteParam;
import cn.oyzh.store.jdbc.JdbcStandardStore;
import cn.oyzh.store.jdbc.QueryParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class ZKGroupStore extends JdbcStandardStore<ZKGroup> {

    /**
     * 当前实例
     */
    public static final ZKGroupStore INSTANCE = new ZKGroupStore();

    public List<ZKGroup> load() {
        return super.selectList();
    }

    public boolean replace(ZKGroup group) {
        if (group != null) {
            if (this.exist(group.getName()) || super.exist(group.getGid())) {
                return this.update(group);
            }
            return this.insert(group);
        }
        return false;
    }

    public boolean delete(String name) {
        if (StringUtil.isNotBlank(name)) {
            DeleteParam param = new DeleteParam();
            param.addQueryParam(new QueryParam("name", name));
            return this.delete(param);
        }
        return false;
    }

    /**
     * 是否存在此分组信息
     *
     * @param name 分组信息
     * @return 结果
     */
    public boolean exist(String name) {
        if (StringUtil.isNotBlank(name)) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            return super.exist(params);
        }
        return false;
    }

    @Override
    protected Class<ZKGroup> modelClass() {
        return ZKGroup.class;
    }
}
