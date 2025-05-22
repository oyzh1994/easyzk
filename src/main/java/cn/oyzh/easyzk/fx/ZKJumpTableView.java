package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKJumpConfig;
import cn.oyzh.fx.plus.controls.table.FXTableView;
import cn.oyzh.fx.plus.tableview.TableViewUtil;

/**
 * @author oyzh
 * @since 2025-04-15
 */
public class ZKJumpTableView extends FXTableView<ZKJumpConfig> {

    {
        TableViewUtil.copyCellDataOnDoubleClicked(this);
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
