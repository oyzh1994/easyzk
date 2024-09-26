package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.common.sqlite.SqliteStore;


/**
 * @author oyzh
 * @since 2024/09/23
 */
public class ZKSettingStore2 extends SqliteStore<ZKSetting> {

    /**
     * 当前实例
     */
    public static final ZKSettingStore2 INSTANCE = new ZKSettingStore2();

    /**
     * 当前设置
     */
    public static final ZKSetting SETTING = INSTANCE.load();

    /**
     * 数据id
     */
    private static final String DATA_UID = "DEFAULT";

    public ZKSetting load() {
        ZKSetting setting = super.selectOne(DATA_UID);
        if (setting == null) {
            setting = new ZKSetting();
        }
        return setting;
    }

    public boolean replace(ZKSetting model) {
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
}
