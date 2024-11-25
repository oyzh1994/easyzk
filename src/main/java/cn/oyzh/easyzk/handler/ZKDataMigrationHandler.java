package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.store.ZKGroupStore2;
import cn.oyzh.easyzk.store.ZKInfoStore2;
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

    private ZKInfoStore2 infoStore = new ZKInfoStore2();

    private ZKGroupStore2 groupStore = new ZKGroupStore2();

    /**
     * 执行传输
     */
    public void doMigration() {
        this.message("正在执行迁移");

        this.message("正在迁移分组...");
        List<ZKGroup> groups = ZKStoreUtil.loadGroups();
        this.message("已找到分组:" + groups.size());
        // 清空分组
        if ("2".equals(this.dataPolicy)) {
            this.groupStore.delete((DeleteParam) null);
            this.message("旧分组数据已清空...");
        }
        for (ZKGroup group : groups) {
            this.groupStore.replace(group);
        }
        this.message("迁移分组成功...");

        this.message("正在迁移连接...");
        List<ZKInfo> connects = ZKStoreUtil.loadConnects();
        this.message("已找到连接:" + connects.size());
        // 清空分组
        if ("2".equals(this.dataPolicy)) {
            this.infoStore.delete((DeleteParam) null);
            this.message("旧连接数据已清空...");
        }
        for (ZKInfo connect : connects) {
            this.infoStore.replace(connect);
        }
        this.message("迁移连接成功...");
    }
}

