package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.store.jdbc.JdbcStore;
import cn.oyzh.common.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class ZKGroupStore2 extends JdbcStore<ZKGroup> {

    /**
     * 当前实例
     */
    public static final ZKGroupStore2 INSTANCE = new ZKGroupStore2();

    public List<ZKGroup> load() {
        return super.selectList();
    }

    public boolean replace(ZKGroup group) {
        if (group != null) {
            if (this.exist(group.getName())) {
                return this.update(group);
            }
            return this.insert(group);
        }
        return false;
    }

    public boolean delete(String name) {
        if (StringUtil.isNotBlank(name)) {
            Map<String, Object> params = new HashMap<>();
            params.put("name", name);
            return this.delete(params);
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
    protected ZKGroup newModel() {
        return new ZKGroup();
    }

    @Override
    protected Class<ZKGroup> modelClass() {
        return ZKGroup.class;
    }
}
