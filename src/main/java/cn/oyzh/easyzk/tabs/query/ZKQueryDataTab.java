package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryDataTab extends DynamicTab {

    public ZKQueryDataTab(String path, byte[] data, ZKClient zkClient) {
        super();
        super.flush();
        this.controller().init(path, data, zkClient);
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryDataTab.fxml";
    }

    @Override
    protected ZKQueryDataTabController controller() {
        return (ZKQueryDataTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.data();
    }


}
