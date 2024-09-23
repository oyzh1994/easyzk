package cn.oyzh.easyzk.store;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.plus.domain.Setting;
import cn.oyzh.fx.plus.store.SettingStore;
import lombok.NonNull;

import java.util.Map;


/**
 *
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
        ZKSetting setting = null;
        try {
            Map<String, Object> record = super.selectOne(DATA_UID);
            if (CollUtil.isNotEmpty(record)) {
                setting = this.toModel(record);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 如果查询失败，则返回一个新的ZKSetting对象
        if (setting == null) {
            setting = new ZKSetting();
        }
        return setting;
    }

    public boolean update(@NonNull ZKSetting setting) throws Exception {
        Map<String, Object> record = this.toRecord(setting);
        if (super.exist(DATA_UID)) {
            return this.update(record, DATA_UID) > 0;
        }
        return this.insert(record) > 0;
    }

    @Override
    protected TableDefinition getTableDefinition() {
        TableDefinition definition = super.getTableDefinition();

        ColumnDefinition authMode = new ColumnDefinition();
        authMode.setColumnName("authMode");
        authMode.setColumnType("byte");

        ColumnDefinition loadMode = new ColumnDefinition();
        loadMode.setColumnName("loadMode");
        loadMode.setColumnType("byte");

        definition.addColumnDefinition(authMode);
        definition.addColumnDefinition(loadMode);

        return definition;
    }

    @Override
    protected Map<String, Object> toRecord(ZKSetting model) {
        Map<String, Object> record = super.toRecord(model);
        record.put("authMode", model.getAuthMode());
        record.put("loadMode", model.getLoadMode());
        return record;
    }

    @Override
    protected ZKSetting toModel(Map<String, Object> record) {
        Setting setting = super.toModel(record);
        ZKSetting zkSetting = new ZKSetting();
        zkSetting.copy(setting);
        return zkSetting;
    }
}
