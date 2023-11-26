package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.common.store.ObjectFileStore;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;


/**
 * zk设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
//@Slf4j
public class ZKSettingStore extends ObjectFileStore<ZKSetting> {

    /**
     * 当前实例
     */
    public static final ZKSettingStore INSTANCE = new ZKSettingStore();

    /**
     * 当前设置
     */
    public static final ZKSetting SETTING = INSTANCE.load();

    {
        this.filePath(ZKConst.STORE_PATH + "zk_setting.json");
        StaticLog.info("ZKSettingStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized ZKSetting load() {
        ZKSetting setting = null;
        try {
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isNotBlank(text)) {
                setting = JSON.parseObject(text, ZKSetting.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
