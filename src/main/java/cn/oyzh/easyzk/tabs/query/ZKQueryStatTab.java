package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.dto.ZKQueryParam;
import cn.oyzh.easyzk.dto.ZKQueryResult;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;
import org.apache.zookeeper.data.Stat;

/**
 * zk更新日志表tab
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ZKQueryStatTab extends DynamicTab {

    public ZKQueryStatTab(Stat stat) {
        super();
        super.flush();
        this.controller().init(stat);
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryStatTab.fxml";
    }

    @Override
    protected ZKQueryStatTabController controller() {
        return (ZKQueryStatTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.stat();
    }

}
