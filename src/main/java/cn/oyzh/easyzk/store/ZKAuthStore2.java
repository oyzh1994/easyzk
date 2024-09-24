package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.fx.common.dto.Paging;
import cn.oyzh.fx.common.store.SqlDataUtil;
import cn.oyzh.fx.common.store.SqliteStore;
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

    public boolean replace(ZKAuth auth) {
        boolean result = false;
        try {
            if (auth != null) {
                if (this.exist(auth.getUser(), auth.getPassword())) {
                    Map<String, Object> record = this.toRecord(auth);
                    result = this.update(record, auth.getUid()) > 0;
                } else {
                    auth.setUid(SqlDataUtil.generateUid());
                    Map<String, Object> record = this.toRecord(auth);
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
            List<ZKAuth> auths = new ArrayList<>();
            for (Map<String, Object> objectMap : list) {
                auths.add(this.toModel(objectMap));
            }
            // 分页对象
            return new Paging<>(auths, limit);
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
    protected TableDefinition getTableDefinition() {
        TableDefinition definition = new TableDefinition();
        definition.setTableName("t_auth");
        ColumnDefinition gid = new ColumnDefinition();
        gid.setColumnName("uid");
        gid.setColumnType("text");
        gid.setPrimaryKey(true);
        ColumnDefinition user = new ColumnDefinition();
        user.setColumnName("user");
        user.setColumnType("text");
        ColumnDefinition password = new ColumnDefinition();
        password.setColumnName("password");
        password.setColumnType("text");
        ColumnDefinition enable = new ColumnDefinition();
        enable.setColumnName("enable");
        enable.setColumnType("integer");
        definition.addColumnDefinition(gid);
        definition.addColumnDefinition(user);
        definition.addColumnDefinition(password);
        definition.addColumnDefinition(enable);
        return definition;
    }

    @Override
    protected ZKAuth toModel(Map<String, Object> record) {
        ZKAuth model = this.newModel();
        model.setUid((String) record.get("uid"));
        model.setUser((String) record.get("user"));
        model.setPassword((String) record.get("password"));
        model.setEnable(SqlDataUtil.toBool(record.get("enable")));
        return model;
    }

    @Override
    protected Map<String, Object> toRecord(ZKAuth model) {
        Map<String, Object> record = new HashMap<>();
        record.put("uid", model.getUid());
        record.put("user", model.getUser());
        record.put("enable", model.getEnable());
        record.put("password", model.getPassword());
        return record;
    }
}
