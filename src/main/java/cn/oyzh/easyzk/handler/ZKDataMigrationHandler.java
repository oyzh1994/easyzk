package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.store.ZKGroupStore2;
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


    private ZKGroupStore2 groupStore = new ZKGroupStore2();

    /**
     * 执行传输
     */
    public void doMigration() {
        this.message("正在执行迁移");
        this.message("正在寻找分组");

        List<ZKGroup> groups = ZKStoreUtil.loadGroups();
        this.message("已找到分组:" + groups.size());
        this.message("正在迁移分组...");
        if ("2".equals(this.dataPolicy)) {
            this.groupStore.delete((DeleteParam) null);
        }
        for (ZKGroup group : groups) {
            this.groupStore.replace(group);
        }
        this.message("迁移分组成功...");
    }
}

