package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.util.FileStore;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSetting;
import com.alibaba.fastjson.JSON;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * zk设置储存
 *
 * @author oyzh
 * @since 2022/8/26
 */
@Slf4j
public class ZKSettingStore extends FileStore<ZKSetting> {

    /**
     * 当前实例
     */
    public static final ZKSettingStore INSTANCE = new ZKSettingStore();

    /**
     * 当前设置
     */
    public static final ZKSetting SETTING = INSTANCE.loadOne();

    {
        this.filePath(ZKConst.STORE_PATH + "zk_setting.json");
        log.info("ZKSettingStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized List<ZKSetting> load() {
        ZKSetting setting = null;
        String text = FileUtil.readString(this.storeFile(), this.charset());
        if (StrUtil.isNotBlank(text)) {
            try {
                setting = JSON.parseObject(text, ZKSetting.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (setting == null) {
            setting = new ZKSetting();
        }
        return List.of(setting);
    }

    @Override
    public boolean add(@NonNull ZKSetting setting) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean update(@NonNull ZKSetting setting) {
        return this.save(setting);
    }

    @Override
    public boolean delete(@NonNull ZKSetting setting) {
        throw new UnsupportedOperationException();
    }
}
