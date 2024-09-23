package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.fx.plus.domain.TreeGroup;
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

    @Override
    protected ZKGroup toModel(Map<String, Object> record) {
        TreeGroup group = super.toModel(record);
        ZKGroup zkGroup = new ZKGroup();
        zkGroup.copy(group);
        return zkGroup;
    }

    public synchronized List<ZKGroup> load() {
        try {
            // 读取存储文件中的文本
            List<Map<String, Object>> records = super.selectList(null);
            if (CollUtil.isEmpty(records)) {
                return new ArrayList<>();
            }
            List<ZKGroup> list = new ArrayList<>();
            for (Map<String, Object> record : records) {
                list.add(this.toModel(record));
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public synchronized boolean replace(ZKGroup group) {
        try {
            if (group == null) {
                return false;
            }
            String id = group.getGid();
            // 更新
            if (id != null && super.exist(id)) {
                Map<String, Object> record = this.toRecord(group);
                return this.update(record, id) > 0;
            }
            // 新增
            if (StrUtil.isBlank(id)) {
                group.setGid(super.uid());
            }
            Map<String, Object> record = this.toRecord(group);
            return this.insert(record) > 0;
        } catch (Exception e) {
            StaticLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    public synchronized boolean delete(ZKGroup group) {
        try {
            if (group != null && group.getGid() != null) {
                String id = group.getGid();
                group.setGid(null);
                return this.delete(id) > 0;
            }
        } catch (Exception e) {
            StaticLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 是否存在此分组信息
     *
     * @param name 分组信息
     * @return 结果
     */
    public synchronized boolean exist(String name) {
        try {
            // 如果传入的分组信息为空，则直接返回false
            if (name == null) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", name);
                return super.exist(params);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
