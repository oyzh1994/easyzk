package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKJumpConfig;
import cn.oyzh.easyzk.store.ZKJumpConfigStore;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import cn.oyzh.fx.plus.tableview.TableViewUtil;

import java.util.List;

/**
 * @author oyzh
 * @since 2025-04-15
 */
public class ZKJumpTableView extends FXTableView<ZKJumpConfig> {

    {
        TableViewUtil.copyCellDataOnDoubleClicked(this);
    }

    /**
     * 跳板配置存储器
     */
    private final ZKJumpConfigStore configStore = ZKJumpConfigStore.INSTANCE;

    public void init(String iid) {
        List<ZKJumpConfig> configs = this.configStore.listByIid(iid);
        this.setItem(configs);
    }

    /**
     * 更新排序
     */
    public void updateOrder() {
        for (int i = 0; i < this.getItemSize(); i++) {
            ZKJumpConfig config = (ZKJumpConfig) this.getItem(i);
            config.setOrder(i);
        }
    }

}
