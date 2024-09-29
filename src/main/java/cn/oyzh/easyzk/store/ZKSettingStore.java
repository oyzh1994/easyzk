package cn.oyzh.easyzk.store;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.common.json.JSONUtil;
import cn.oyzh.fx.common.store.ObjectFileStore;
import cn.oyzh.fx.common.util.FileUtil;
import cn.oyzh.fx.common.util.StringUtil;
import lombok.NonNull;


/**
 * zk设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
@Deprecated
public class ZKSettingStore extends ObjectFileStore<ZKSetting> {

    /**
     * 当前实例
     */
    public static final ZKSettingStore INSTANCE = new ZKSettingStore();

    // /**
    //  * 当前设置
    //  */
    // public static final ZKSetting SETTING = INSTANCE.load();
    //
    // {
    //     this.filePath(ZKConst.STORE_PATH + "zk_setting.json");
    //     JulLog.info("ZKSettingStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    // }

    public ZKSettingStore() {
        this.filePath(ZKConst.STORE_PATH + "zk_setting.json");
    }

    @Override
    public synchronized ZKSetting load() {
        ZKSetting setting = null;
        try {
            // 读取配置文件内容
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StringUtil.isNotBlank(text)) {
                // 将配置文件内容解析为ZKSetting对象
                setting = JSONUtil.toBean(text, ZKSetting.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 如果解析失败，则返回一个新的ZKSetting对象
        if (setting == null) {
            setting = new ZKSetting();
        }
        return setting;
    }

    @Override
    public boolean update(@NonNull ZKSetting setting) {
        return this.saveData(setting);
    }
}
