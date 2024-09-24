package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.common.sqlite.SqliteStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * zk信息存储
 *
 * @author oyzh
 * @since 2020/5/23
 */
public class ZKInfoStore2 extends SqliteStore<ZKInfo> {

    /**
     * 当前实例
     */
    public static final ZKInfoStore2 INSTANCE = new ZKInfoStore2();

    public synchronized List<ZKInfo> load() {
        try {
            // 读取内容
            List<Map<String, Object>> records = this.selectList(null);
            // 如果文件内容为空
            if (CollUtil.isEmpty(records)) {
                // 返回空列表
                return new ArrayList<>();
            }
            List<ZKInfo> list = new ArrayList<>();
            for (Map<String, Object> record : records) {
                list.add(this.toModel(record));
            }
            return list;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    public synchronized boolean add(ZKInfo zkInfo) {
        try {
            if (zkInfo == null) {
                return false;
            }
            String id = zkInfo.getId();
            if (id != null && this.exist(id)) {
                return false;
            }
            if (StrUtil.isBlank(zkInfo.getId())) {
                zkInfo.setId(UUID.fastUUID().toString(true));
            }
            Map<String, Object> record = this.toRecord(zkInfo);
            // 新增数据
            return this.insert(record) > 0;
        } catch (Exception e) {
            StaticLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    public synchronized boolean delete(ZKInfo zkInfo) {
        try {
            // 删除数据
            if (zkInfo != null && zkInfo.getId() != null) {
                return this.delete(zkInfo.getId()) > 0;
            }
        } catch (Exception e) {
            StaticLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected ZKInfo newModel() {
        return null;
    }

    @Override
    protected Class<ZKInfo> modelClass() {
        return null;
    }

    // @Override
    // protected TableDefinition getTableDefinition() {
    //     TableDefinition definition = new TableDefinition();
    //     definition.setTableName("info");
    //
    //     ColumnDefinition id = new ColumnDefinition();
    //     id.setColumnName("id");
    //     id.setColumnType("char");
    //     id.setPrimaryKey(true);
    //     definition.addColumnDefinition(id);
    //
    //     // 主题
    //     ColumnDefinition theme = new ColumnDefinition();
    //     theme.setColumnName("theme");
    //     theme.setColumnType("char");
    //     ColumnDefinition bgColor = new ColumnDefinition();
    //     bgColor.setColumnName("bgColor");
    //     bgColor.setColumnType("char");
    //     ColumnDefinition fgColor = new ColumnDefinition();
    //     fgColor.setColumnName("fgColor");
    //     fgColor.setColumnType("char");
    //     ColumnDefinition accentColor = new ColumnDefinition();
    //     accentColor.setColumnName("accentColor");
    //     accentColor.setColumnType("char");
    //     definition.addColumnDefinition(theme);
    //     definition.addColumnDefinition(bgColor);
    //     definition.addColumnDefinition(fgColor);
    //     definition.addColumnDefinition(accentColor);
    //
    //     // 基本
    //     ColumnDefinition locale = new ColumnDefinition();
    //     locale.setColumnName("locale");
    //     locale.setColumnType("char");
    //     ColumnDefinition exitMode = new ColumnDefinition();
    //     exitMode.setColumnName("exitMode");
    //     exitMode.setColumnType("integer");
    //     ColumnDefinition opacity = new ColumnDefinition();
    //     opacity.setColumnName("opacity");
    //     opacity.setColumnType("double");
    //     definition.addColumnDefinition(locale);
    //     definition.addColumnDefinition(exitMode);
    //     definition.addColumnDefinition(opacity);
    //
    //     // tab
    //     ColumnDefinition tabLimit = new ColumnDefinition();
    //     tabLimit.setColumnName("tabLimit");
    //     tabLimit.setColumnType("integer");
    //     ColumnDefinition tabStrategy = new ColumnDefinition();
    //     tabStrategy.setColumnName("tabStrategy");
    //     tabStrategy.setColumnType("char");
    //     definition.addColumnDefinition(tabLimit);
    //     definition.addColumnDefinition(tabStrategy);
    //
    //     // 字体
    //     ColumnDefinition fontSize = new ColumnDefinition();
    //     fontSize.setColumnName("fontSize");
    //     fontSize.setColumnType("integer");
    //     ColumnDefinition fontWeight = new ColumnDefinition();
    //     fontWeight.setColumnName("fontWeight");
    //     fontWeight.setColumnType("integer");
    //     ColumnDefinition fontFamily = new ColumnDefinition();
    //     fontFamily.setColumnName("fontFamily");
    //     fontFamily.setColumnType("char");
    //     definition.addColumnDefinition(fontSize);
    //     definition.addColumnDefinition(fontWeight);
    //     definition.addColumnDefinition(fontFamily);
    //
    //     // 页面
    //     ColumnDefinition rememberPageSize = new ColumnDefinition();
    //     rememberPageSize.setColumnName("rememberPageSize");
    //     rememberPageSize.setColumnType("integer");
    //     ColumnDefinition rememberPageResize = new ColumnDefinition();
    //     rememberPageResize.setColumnName("rememberPageResize");
    //     rememberPageResize.setColumnType("integer");
    //     ColumnDefinition rememberPageLocation = new ColumnDefinition();
    //     rememberPageLocation.setColumnName("rememberPageLocation");
    //     rememberPageLocation.setColumnType("char");
    //     definition.addColumnDefinition(rememberPageSize);
    //     definition.addColumnDefinition(rememberPageResize);
    //     definition.addColumnDefinition(rememberPageLocation);
    //     return definition;
    // }
    //
    // @Override
    // protected Map<String, Object> toRecord(ZKInfo model) {
    //     Map<String, Object> record = new HashMap<>();
    //     record.put("id", model.getId());
    //     record.put("host", model.getHost());
    //     record.put("name", model.getName());
    //     record.put("remark", model.getRemark());
    //     record.put("listen", model.getListen());
    //     record.put("groupId", model.getGroupId());
    //     record.put("readonly", model.isReadonly());
    //     record.put("sshForward", model.getSshForward());
    //     record.put("compatibility", model.getCompatibility());
    //     record.put("sessionTimeOut", model.getSessionTimeOut());
    //     record.put("connectTimeOut", model.getConnectTimeOut());
    //     return record;
    // }
    //
    // @Override
    // protected ZKInfo toModel(Map<String, Object> record) {
    //     ZKInfo model =  new ZKInfo();
    //
    //     return model;
    // }
}
