package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * zk更新日志表tab
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ZKQueryNodeTab extends DynamicTab {

    public ZKQueryNodeTab(String path, List<String> nodes) {
        super();
        super.flush();
        this.controller().init(path, nodes);
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryNodeTab.fxml";
    }

    @Override
    protected ZKQueryNodeTabController controller() {
        return (ZKQueryNodeTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.node();
    }

}
