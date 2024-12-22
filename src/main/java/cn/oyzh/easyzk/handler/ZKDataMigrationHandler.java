package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKAuthStore;
import cn.oyzh.easyzk.store.ZKConnectStore;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.store.ZKStoreUtil;
import cn.oyzh.easyzk.terminal.ZKTerminalHistory;
import cn.oyzh.easyzk.terminal.ZKTerminalHistoryJdbcStore;
import cn.oyzh.easyzk.util.ZKI18nHelper;
import cn.oyzh.i18n.I18nHelper;
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

    private ZKAuthStore authStore = ZKAuthStore.INSTANCE;

    private ZKConnectStore infoStore = ZKConnectStore.INSTANCE;

    private ZKGroupStore groupStore = ZKGroupStore.INSTANCE;

    private ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    private ZKSettingStore settingStore = ZKSettingStore.INSTANCE;

    private ZKTerminalHistoryJdbcStore terminalHistoryStore = ZKTerminalHistoryJdbcStore.INSTANCE;

    /**
     * 执行传输
     */
    public void doMigration() {
        this.message("Migration Starting");

        if (this.groups) {
            this.message(I18nHelper.migratingGroups());
            List<ZKGroup> groups = ZKStoreUtil.loadGroups();
            this.message(I18nHelper.foundGroup() + " : " + groups.size());
            if ("1".equals(this.dataPolicy)) {
                for (ZKGroup group : groups) {
                    if (!this.groupStore.exist(group.getName())) {
                        this.groupStore.replace(group);
                        this.message(I18nHelper.group() + " : " + group.getName() + " " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.groupStore.clear();
                this.message(I18nHelper.groupDataCleared());
                for (ZKGroup group : groups) {
                    this.groupStore.replace(group);
                }
                this.processedIncr(groups.size());
            }
            this.message(I18nHelper.migrationGroupsSuccessful());
        }

        if (this.connections) {
            this.message(I18nHelper.migratingConnections());
            List<ZKConnect> connects = ZKStoreUtil.loadConnects();
            this.message(I18nHelper.foundConnection() + " : " + connects.size());
            if ("1".equals(this.dataPolicy)) {
                for (ZKConnect connect : connects) {
                    if (!this.infoStore.exist(connect.getId())) {
                        this.infoStore.replace(connect);
                        this.message(I18nHelper.connect() + " : " + connect.getName() + " " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.infoStore.clear();
                this.message(I18nHelper.connectionDataCleared());
                for (ZKConnect connect : connects) {
                    this.infoStore.replace(connect);
                }
                this.processedIncr(connects.size());
            }
            this.message(I18nHelper.migrationConnectionsSuccessful());
        }

        if (this.filters) {
            this.message(I18nHelper.migratingFilters());
            List<ZKFilter> filters = ZKStoreUtil.loadFilters();
            this.message(I18nHelper.foundFilter() + " : " + filters.size());
            if ("1".equals(this.dataPolicy)) {
                for (ZKFilter filter : filters) {
                    if (!this.filterStore.exist(filter.getKw())) {
                        this.filterStore.replace(filter);
                        this.message(I18nHelper.filter() + " : " + filter.getKw() + " " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.filterStore.clear();
                this.message(I18nHelper.filterDataCleared());
                for (ZKFilter filter : filters) {
                    this.filterStore.replace(filter);
                }
                this.processedIncr(filters.size());
            }
            this.message(I18nHelper.migrationFiltersSuccessful());
        }

        if (this.authInfos) {
            this.message(I18nHelper.migratingAuth());
            List<ZKAuth> auths = ZKStoreUtil.loadAuths();
            this.message(I18nHelper.foundAuth() + " : " + auths.size());
            if ("1".equals(this.dataPolicy)) {
                for (ZKAuth auth : auths) {
                    if (!this.authStore.exist(auth.getUser(), auth.getPassword(), auth.getIid())) {
                        this.authStore.replace(auth);
                        this.message(I18nHelper.auth() + " : [" + auth.getUser() + "," + auth.getPassword() + "] " + I18nHelper.added());
                        this.processedIncr();
                    }
                }
            } else if ("2".equals(this.dataPolicy)) {
                this.authStore.clear();
                this.message(I18nHelper.authDataCleared());
                for (ZKAuth auth : auths) {
                    this.authStore.replace(auth);
                }
                this.processedIncr(auths.size());
            }
            this.message(I18nHelper.migrationAuthSuccessful());
        }

        if (this.terminalHistory) {
            this.message(I18nHelper.migratingTerminalHistory());
            List<ZKTerminalHistory> terminalHistories = ZKStoreUtil.loadTerminalHistory();
            this.message(I18nHelper.foundTerminalHistory() + " : " + terminalHistories.size());
            // 清理终端
            if ("2".equals(this.dataPolicy)) {
                this.terminalHistoryStore.clear();
            }
            this.message(I18nHelper.terminalHistoryDataCleared());
            for (ZKTerminalHistory history : terminalHistories) {
                this.terminalHistoryStore.replace(history);
            }
            this.processedIncr(terminalHistories.size());
            this.message(I18nHelper.migrationTerminalHistorySuccessful());
        }

        if (this.applicationSetting) {
            this.message(I18nHelper.migratingApplicationSetting());
            ZKSetting setting = ZKStoreUtil.loadSetting();
            // 清理设置
            if ("2".equals(this.dataPolicy)) {
                this.settingStore.clear();
                this.message(I18nHelper.applicationSettingDataCleared());
            }
            this.settingStore.replace(setting);
            this.processedIncr();
            this.message(I18nHelper.migrationApplicationSettingSuccessful());
        }

        this.message("Migration Finished");
        this.message(ZKI18nHelper.migrationTip1());
    }
}

