package cn.oyzh.easyzk.store;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.fx.plus.store.GroupStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class ZKGroupStore2 extends GroupStore<ZKGroup> {

    /**
     * 当前实例
     */
    public static final ZKGroupStore2 INSTANCE = new ZKGroupStore2();

    public List<ZKGroup> load() {
        return super.selectList(null);
    }

    public boolean replace(ZKGroup group) {
        boolean result = false;
        if (group != null) {
            if (this.exist(group.getName())) {
                result = this.update(group);
            } else {
                result = this.insert(group);
            }
        }
        return result;
    }

    public boolean updateExtend(ZKGroup group) {
        boolean result = false;
        if (group != null) {
            result = this.update(group);
        }
        return result;
    }

    public boolean delete(String name) {
        if (StrUtil.isNotBlank(name)) {
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
        if (StrUtil.isNotBlank(name)) {
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
