package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.dto.ZKEnvNode;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.i18n.I18nHelper;

import java.util.List;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryEnvTab extends RichTab {

    public ZKQueryEnvTab(List<ZKEnvNode> envNodes) {
        super();
        super.flush();
        this.controller().init(envNodes);
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryEnvTab.fxml";
    }

    @Override
    protected ZKQueryEnvTabController controller() {
        return (ZKQueryEnvTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.env();
    }

}
