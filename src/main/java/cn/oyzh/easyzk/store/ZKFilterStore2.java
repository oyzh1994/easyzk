package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.sqlite.PageParam;
import cn.oyzh.fx.common.sqlite.QueryParam;
import cn.oyzh.fx.common.sqlite.SqlLiteUtil;
import cn.oyzh.fx.common.sqlite.SqliteStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024/09/24
 */
public class ZKFilterStore2 extends SqliteStore<ZKFilter> {

    /**
     * 当前实例
     */
    public static final ZKFilterStore2 INSTANCE = new ZKFilterStore2();

    public List<ZKFilter> load() {
        try {
            List<Map<String, Object>> records = super.selectList();
            if (CollUtil.isNotEmpty(records)) {
                List<ZKFilter> list = new ArrayList<>();
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

    /**
     * 加载已启用的数据列表
     *
     * @return 已启用的数据列表
     */
    public synchronized List<ZKFilter> loadEnable() {
        try {
            List<QueryParam> params = new ArrayList<>();
            params.add(new QueryParam("enable", true));
            List<Map<String, Object>> records = super.selectList(params);
            if (CollUtil.isNotEmpty(records)) {
                List<ZKFilter> list = new ArrayList<>();
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

    public boolean replace(ZKFilter model) {
        boolean result = false;
        try {
            if (model != null) {
                if (this.exist(model.getKw())) {
                    result = this.update(model) > 0;
                } else {
                    result = this.insert(model) > 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public boolean delete(String kw) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("kw", kw);
            return this.delete(params) > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Paging<ZKFilter> getPage(long pageNo, int limit, String kw) {
        try {
            PageParam pageParam = new PageParam(limit, pageNo * limit);
            List<Map<String, Object>> list = this.selectPage(kw, List.of("kw"), pageParam);
            if (CollUtil.isNotEmpty(list)) {
                long count = this.selectCount(kw, List.of("kw"));
                List<ZKFilter> dataList = new ArrayList<>();
                for (Map<String, Object> objectMap : list) {
                    dataList.add(this.toModel(objectMap));
                }
                return new Paging<>(dataList, limit, count);
            }
            return new Paging<>(limit);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public boolean exist(@NonNull String kw) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("kw", kw);
            return super.exist(params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected ZKFilter newModel() {
        return new ZKFilter();
    }

    @Override
    protected Class<ZKFilter> modelClass() {
        return ZKFilter.class;
    }

    //
    // @Override
    // protected TableDefinition getTableDefinition() {
    //     TableDefinition definition = new TableDefinition();
    //     definition.setTableName("t_filter");
    //     ColumnDefinition uid = new ColumnDefinition();
    //     uid.setColumnName("uid");
    //     uid.setColumnType("text");
    //     uid.setPrimaryKey(true);
    //     ColumnDefinition kw = new ColumnDefinition();
    //     kw.setColumnName("kw");
    //     kw.setColumnType("text");
    //     ColumnDefinition partMatch = new ColumnDefinition();
    //     partMatch.setColumnName("partMatch");
    //     partMatch.setColumnType("integer");
    //     ColumnDefinition enable = new ColumnDefinition();
    //     enable.setColumnName("enable");
    //     enable.setColumnType("integer");
    //     definition.addColumnDefinition(kw);
    //     definition.addColumnDefinition(uid);
    //     definition.addColumnDefinition(enable);
    //     definition.addColumnDefinition(partMatch);
    //     return definition;
    // }
    //
    // @Override
    // protected ZKFilter toModel(Map<String, Object> record) {
    //     ZKFilter model = this.newModel();
    //     model.setKw((String) record.get("kw"));
    //     model.setUid((String) record.get("uid"));
    //     model.setEnable(SqlLiteUtil.toBoolVal(record.get("enable")));
    //     model.setPartMatch(SqlLiteUtil.toBoolVal(record.get("partMatch")));
    //     return model;
    // }
    //
    // @Override
    // protected Map<String, Object> toRecord(ZKFilter model) {
    //     Map<String, Object> record = new HashMap<>();
    //     record.put("kw", model.getKw());
    //     record.put("uid", model.getUid());
    //     record.put("enable", model.isEnable());
    //     record.put("partMatch", model.isPartMatch());
    //     return record;
    // }
}
