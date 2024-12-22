package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.store.jdbc.JdbcKeyValueStore;


/**
 * @author oyzh
 * @since 2024/09/23
 */
public class ZKSettingStore extends JdbcKeyValueStore<ZKSetting> {

    /**
     * 当前实例
     */
    public static final ZKSettingStore INSTANCE = new ZKSettingStore();

    /**
     * 当前设置
     */
    public static final ZKSetting SETTING = INSTANCE.load();

    public ZKSetting load() {
        ZKSetting setting = super.select();
        if (setting == null) {
            setting = new ZKSetting();
        }
        return setting;
    }

    public boolean replace(ZKSetting model) {
        if (model != null) {
            return this.update(model);
        }
        return false;
    }

    @Override
    protected Class<ZKSetting> modelClass() {
        return ZKSetting.class;
    }
}
