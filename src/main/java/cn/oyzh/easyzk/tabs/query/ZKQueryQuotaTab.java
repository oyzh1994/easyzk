package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;
import org.apache.zookeeper.StatsTrack;
import org.apache.zookeeper.data.Stat;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryQuotaTab extends DynamicTab {

    public ZKQueryQuotaTab(StatsTrack track) {
        super();
        super.flush();
        this.controller().init(track);
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryQuotaTab.fxml";
    }

    @Override
    protected ZKQueryQuotaTabController controller() {
        return (ZKQueryQuotaTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.quota();
    }

}
