package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.plus.store.SettingStore;


/**
 * @author oyzh
 * @since 2024/09/23
 */
public class ZKSettingStore2 extends SettingStore<ZKSetting> {

    /**
     * 当前实例
     */
    public static final ZKSettingStore2 INSTANCE = new ZKSettingStore2();

    /**
     * 当前设置
     */
    public static final ZKSetting SETTING = INSTANCE.load();

    public ZKSettingStore2() {
        super();
    }

    public synchronized ZKSetting load() {
        ZKSetting setting = super.selectOne(DATA_UID);
        if (setting == null) {
            setting = new ZKSetting();
        }
        return setting;
    }

    public boolean replace(ZKSetting model) throws Exception {
        if (model != null) {
            if (super.exist(DATA_UID)) {
                return this.update(model);
            }
            return this.insert(model);
        }
        return false;
    }

    @Override
    protected ZKSetting newModel() {
        return new ZKSetting();
    }

    @Override
    protected Class<ZKSetting> modelClass() {
        return ZKSetting.class;
    }

    // @Override
    // protected TableDefinition tableDefinition() {
    //     TableDefinition definition = super.getTableDefinition();
    //
    //     ColumnDefinition authMode = new ColumnDefinition();
    //     authMode.setColumnName("authMode");
    //     authMode.setColumnType("integer");
    //
    //     ColumnDefinition loadMode = new ColumnDefinition();
    //     loadMode.setColumnName("loadMode");
    //     loadMode.setColumnType("integer");
    //
    //     definition.addColumnDefinition(authMode);
    //     definition.addColumnDefinition(loadMode);
    //
    //     return definition;
    // }
    //
    // @Override
    // protected Map<String, Object> toRecord(ZKSetting model) {
    //     Map<String, Object> record = super.toRecord(model);
    //     record.put("authMode", model.getAuthMode());
    //     record.put("loadMode", model.getLoadMode());
    //     return record;
    // }
    //
    // @Override
    // protected ZKSetting toModel(Map<String, Object> record) {
    //     ZKSetting setting = super.toModel(record);
    //     setting.setAuthMode(SqlLiteUtil.toByte(record.get("authMode")));
    //     setting.setLoadMode(SqlLiteUtil.toByte(record.get("loadMode")));
    //     return setting;
    // }
}
