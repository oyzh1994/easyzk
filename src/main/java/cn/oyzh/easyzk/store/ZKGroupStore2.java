package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.fx.common.store.SqlDataUtil;
import cn.oyzh.fx.plus.store.GroupStore;

import java.util.ArrayList;
import java.util.Collections;
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
        try {
            List<Map<String, Object>> records = super.selectList(null);
            if (CollUtil.isNotEmpty(records)) {
                List<ZKGroup> list = new ArrayList<>();
                for (Map<String, Object> record : records) {
                    list.add(this.toModel(record));
                }
                return list;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public boolean replace(ZKGroup group) {
        boolean result = false;
        try {
            if (group != null) {
                if (this.exist(group.getName())) {
                    Map<String, Object> record = this.toRecord(group);
                    result = this.update(record, group.getGid()) > 0;
                } else {
                    group.setGid(SqlDataUtil.generateUid());
                    Map<String, Object> record = this.toRecord(group);
                    result = this.insert(record) > 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public boolean updateExtend(ZKGroup group) {
        boolean result = false;
        try {
            if (group != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("gid", group.getGid());
                record.put("extend", group.getExpand());
                result = this.update(record, group.getGid()) > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public boolean delete(String name) {
        try {
            if (StrUtil.isNotBlank(name)) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", name);
                return this.delete(params) > 0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * 是否存在此分组信息
     *
     * @param name 分组信息
     * @return 结果
     */
    public boolean exist(String name) {
        try {
            if (StrUtil.isNotBlank(name)) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", name);
                return super.exist(params);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected ZKGroup newModel() {
        return new ZKGroup();
    }
}
