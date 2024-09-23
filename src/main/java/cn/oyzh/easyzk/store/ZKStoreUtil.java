package cn.oyzh.easyzk.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.common.store.SqliteStore;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-23
 */
@UtilityClass
public class ZKStoreUtil {

    /**
     * 执行初始化
     */
    public static void init() {
        try {
            SqliteStore.initStore(ZKConst.STORE_PATH + "easyzk.db");
        } catch (Exception ex) {
            ex.printStackTrace();
            // 提示
            MessageBox.error(I18nHelper.initStoreFail());
            // 退出程序
            ThreadUtil.start(() -> System.exit(-1), 3000);
        }
    }

    public static void migration() {
        try {
            // 迁移配置
            if (FileUtil.exist(ZKSettingStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKSettingStore.INSTANCE.init();
                // 读取配置
                ZKSetting setting = ZKSettingStore.INSTANCE.load();
                // 复制配置
                ZKSettingStore2.SETTING.copy(setting);
                // 执行迁移
                ZKSettingStore2.INSTANCE.replace(setting);
                // 转移旧文件
                FileUtil.move(new File(ZKSettingStore.INSTANCE.filePath()), new File(ZKSettingStore.INSTANCE.filePath() + ".bak"), true);
                StaticLog.info("配置文件迁移成功");
            }

            // 迁移分组
            if (FileUtil.exist(ZKGroupStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKGroupStore.INSTANCE.init();
                // 读取配置
                List<ZKGroup> groups = ZKGroupStore.INSTANCE.load();
                // 执行迁移
                for (ZKGroup group : groups) {
                    ZKGroupStore2.INSTANCE.replace(group);
                }
                // 转移旧文件
                FileUtil.move(new File(ZKGroupStore.INSTANCE.filePath()), new File(ZKGroupStore.INSTANCE.filePath() + ".bak"), true);
                StaticLog.info("分组文件迁移成功");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // 提示
            MessageBox.error(I18nHelper.initConfigFail());
            // 退出程序
            ThreadUtil.start(() -> System.exit(-1), 3000);
        }
    }
}
