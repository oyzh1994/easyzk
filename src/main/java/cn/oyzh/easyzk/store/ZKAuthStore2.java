package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.sqlite.ColumnDefinition;
import cn.oyzh.fx.common.sqlite.PageParam;
import cn.oyzh.fx.common.sqlite.SqlLiteUtil;
import cn.oyzh.fx.common.sqlite.SqliteStore;
import cn.oyzh.fx.common.sqlite.TableDefinition;
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
public class ZKAuthStore2 extends SqliteStore<ZKAuth> {

    /**
     * 当前实例
     */
    public static final ZKAuthStore2 INSTANCE = new ZKAuthStore2();

    public List<ZKAuth> load() {
        try {
            List<Map<String, Object>> records = super.selectList();
            if (CollUtil.isNotEmpty(records)) {
                List<ZKAuth> list = new ArrayList<>();
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

    public boolean replace(ZKAuth model) {
        boolean result = false;
        try {
            if (model != null) {
                if (this.exist(model.getUser(), model.getPassword())) {
                    Map<String, Object> record = this.toRecord(model);
                    result = this.update(record, model.getUid()) > 0;
                } else {
                    model.setUid(SqlLiteUtil.generateUid());
                    Map<String, Object> record = this.toRecord(model);
                    result = this.insert(record) > 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public boolean delete(String user, String password) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user", user);
            params.put("password", password);
            return this.delete(params) > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public Paging<ZKAuth> getPage(long pageNo, int limit, String kw) {
        try {
            PageParam pageParam = new PageParam(limit, pageNo * limit);
            List<Map<String, Object>> list = this.selectPage(kw, List.of("user", "password"), pageParam);
            if (CollUtil.isNotEmpty(list)) {
                long count = this.selectCount(kw, List.of("kw"));
                List<ZKAuth> dataList = new ArrayList<>();
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

    public boolean exist(@NonNull String user, @NonNull String password) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("user", user);
            params.put("password", password);
            return super.exist(params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    protected ZKAuth newModel() {
        return new ZKAuth();
    }

    @Override
    protected Class<ZKAuth> modelClass() {
        return ZKAuth.class;
    }

    // @Override
    // protected TableDefinition tableDefinition() {
    //     TableDefinition definition = new TableDefinition();
    //     definition.setTableName("t_auth");
    //     ColumnDefinition gid = new ColumnDefinition();
    //     gid.setColumnName("uid");
    //     gid.setColumnType("text");
    //     gid.setPrimaryKey(true);
    //     ColumnDefinition user = new ColumnDefinition();
    //     user.setColumnName("user");
    //     user.setColumnType("text");
    //     ColumnDefinition password = new ColumnDefinition();
    //     password.setColumnName("password");
    //     password.setColumnType("text");
    //     ColumnDefinition enable = new ColumnDefinition();
    //     enable.setColumnName("enable");
    //     enable.setColumnType("integer");
    //     definition.addColumnDefinition(gid);
    //     definition.addColumnDefinition(user);
    //     definition.addColumnDefinition(password);
    //     definition.addColumnDefinition(enable);
    //     return definition;
    // }

    // @Override
    // protected ZKAuth toModel(Map<String, Object> record) {
    //     ZKAuth model = this.newModel();
    //     model.setUid((String) record.get("uid"));
    //     model.setUser((String) record.get("user"));
    //     model.setPassword((String) record.get("password"));
    //     model.setEnable(SqlLiteUtil.toBool(record.get("enable")));
    //     return model;
    // }
    //
    // @Override
    // protected Map<String, Object> toRecord(ZKAuth model) {
    //     Map<String, Object> record = new HashMap<>();
    //     record.put("uid", model.getUid());
    //     record.put("user", model.getUser());
    //     record.put("enable", model.getEnable());
    //     record.put("password", model.getPassword());
    //     return record;
    // }
}
