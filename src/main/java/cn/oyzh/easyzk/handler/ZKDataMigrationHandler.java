package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.terminal.ZKTerminalHistory;
import cn.oyzh.easyzk.store.ZKAuthStore2;
import cn.oyzh.easyzk.store.ZKFilterStore2;
import cn.oyzh.easyzk.store.ZKGroupStore2;
import cn.oyzh.easyzk.store.ZKInfoStore2;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.easyzk.terminal.ZKTerminalHistoryJdbcStore;
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
    private boolean terminalHistory;

    @Setter
    @Accessors(chain = false, fluent = true)
    private boolean applicationSetting;

    /**
     * 1: 合并 2: 覆盖
     */
    @Setter
    @Accessors(chain = false, fluent = true)
    private String dataPolicy;

    private ZKAuthStore2 authStore = ZKAuthStore2.INSTANCE;

    private ZKInfoStore2 infoStore = ZKInfoStore2.INSTANCE;

    private ZKGroupStore2 groupStore = ZKGroupStore2.INSTANCE;

    private ZKFilterStore2 filterStore = ZKFilterStore2.INSTANCE;

    private ZKSettingStore2 settingStore = ZKSettingStore2.INSTANCE;

    private ZKTerminalHistoryJdbcStore terminalHistoryStore = ZKTerminalHistoryJdbcStore.INSTANCE;

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
                this.groupStore.clear();
                this.message("旧分组数据已清空...");
                for (ZKGroup group : groups) {
                    this.groupStore.replace(group);
                }
                this.processedIncr(groups.size());
            }
            this.message("迁移分组成功...");
        }

        if (this.connections) {
            this.message("正在迁移连接...");
            List<ZKInfo> connects = ZKStoreUtil.loadConnects();
            this.message("已找到连接:" + connects.size());
            if ("2".equals(this.dataPolicy)) {
                this.infoStore.clear();
                this.message("旧连接数据已清空...");
                for (ZKInfo connect : connects) {
                    this.infoStore.replace(connect);
                }
                this.processedIncr(connects.size());
            }
            this.message("迁移连接成功...");
        }

        if (this.filters) {
            this.message("正在迁移过滤...");
            List<ZKFilter> filters = ZKStoreUtil.loadFilters();
            this.message("已找到过滤:" + filters.size());
            if ("2".equals(this.dataPolicy)) {
                this.filterStore.clear();
                this.message("旧过滤数据已清空...");
                for (ZKFilter filter : filters) {
                    this.filterStore.replace(filter);
                }
                this.processedIncr(filters.size());
            }
            this.message("迁移过滤成功...");
        }

        if (this.authInfos) {
            this.message("正在迁移认证...");
            List<ZKAuth> auths = ZKStoreUtil.loadAuths();
            this.message("已找到认证:" + auths.size());
            if ("2".equals(this.dataPolicy)) {
                this.authStore.clear();
                this.message("旧认证数据已清空...");
                for (ZKAuth auth : auths) {
                    this.authStore.replace(auth);
                }
                this.processedIncr(auths.size());
            }
            this.message("迁移认证成功...");
        }

        if (this.terminalHistory) {
            this.message("正在迁移终端历史...");
            List<ZKTerminalHistory> terminalHistories = ZKStoreUtil.loadTerminalHistory();
            this.message("已找到终端历史:" + terminalHistories.size());
            if ("2".equals(this.dataPolicy)) {
                this.terminalHistoryStore.clear();
                this.message("旧终端历史数据已清空...");
                for (ZKTerminalHistory history : terminalHistories) {
                    this.terminalHistoryStore.replace(history);
                }
                this.processedIncr(terminalHistories.size());
            }
            this.message("迁移终端历史成功...");
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
                this.processedIncr();
            }
            this.message("迁移应用设置成功...");
        }

        this.message("迁移执行完成，请重写打开应用以生效");
    }
}

