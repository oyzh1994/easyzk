package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKAuthStore2;
import cn.oyzh.easyzk.store.ZKFilterStore2;
import cn.oyzh.easyzk.store.ZKGroupStore2;
import cn.oyzh.easyzk.store.ZKInfoStore2;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.store.jdbc.DeleteParam;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author oyzh
 * @since 2024/10/15
 */
@Setter
public class ZKDataMigrationHandler extends DataHandler {

    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean groups;

    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean filters;

    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean authInfos;

    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean connections;

    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean applicationSetting;

    /**
     * 1: 合并 2: 覆盖
     */
    @Setter
    @Accessors(chain = false, fluent = true)
    private String dataPolicy;

    private ZKAuthStore2 authStore = new ZKAuthStore2();

    private ZKInfoStore2 infoStore = new ZKInfoStore2();

    private ZKGroupStore2 groupStore = new ZKGroupStore2();

    private ZKFilterStore2 filterStore = new ZKFilterStore2();

    private ZKSettingStore2 settingStore = new ZKSettingStore2();

    /**
     * 执行传输
     */
    public void doMigration() {
        this.message("正在执行迁移");

        if (this.groups) {
            this.message("正在迁移分组...");
            List<ZKGroup> groups = ZKStoreUtil.loadGroups();
            this.message("已找到分组:" + groups.size());
            if ("2".equals(this.dataPolicy)) {
                this.groupStore.delete((DeleteParam) null);
                this.message("旧分组数据已清空...");
                for (ZKGroup group : groups) {
                    this.groupStore.replace(group);
                }
            }
            this.message("迁移分组成功...");
        }

        if (this.connections) {
            this.message("正在迁移连接...");
            List<ZKInfo> connects = ZKStoreUtil.loadConnects();
            this.message("已找到连接:" + connects.size());
            if ("2".equals(this.dataPolicy)) {
                this.infoStore.delete((DeleteParam) null);
                this.message("旧连接数据已清空...");
                for (ZKInfo connect : connects) {
                    this.infoStore.replace(connect);
                }
            }
            this.message("迁移连接成功...");
        }

        if (this.filters) {
            this.message("正在迁移过滤...");
            List<ZKFilter> filters = ZKStoreUtil.loadFilters();
            this.message("已找到过滤:" + filters.size());
            if ("2".equals(this.dataPolicy)) {
                this.filterStore.delete((DeleteParam) null);
                this.message("旧过滤数据已清空...");
                for (ZKFilter filter : filters) {
                    this.filterStore.replace(filter);
                }
            }
            this.message("迁移过滤成功...");
        }

        if (this.authInfos) {
            this.message("正在迁移认证...");
            List<ZKAuth> auths = ZKStoreUtil.loadAuths();
            this.message("已找到认证:" + auths.size());
            if ("2".equals(this.dataPolicy)) {
                this.authStore.delete((DeleteParam) null);
                this.message("旧认证数据已清空...");
                for (ZKAuth auth : auths) {
                    this.authStore.replace(auth);
                }
            }
            this.message("迁移认证成功...");
        }

        if (this.applicationSetting) {
            this.message("正在迁移应用设置...");
            ZKSetting setting = ZKStoreUtil.loadSetting();
            if (setting == null) {
                this.message("未找到应用设置");
            } else if ("2".equals(this.dataPolicy)) {
                this.message("已找到应用设置");
                this.settingStore.delete((DeleteParam) null);
                this.message("旧应用设置数据已清空...");
                this.settingStore.replace(setting);
            }
            this.message("迁移应用设置成功...");
        }

        this.message("迁移执行完成，请重写打开应用以生效");
    }
}

