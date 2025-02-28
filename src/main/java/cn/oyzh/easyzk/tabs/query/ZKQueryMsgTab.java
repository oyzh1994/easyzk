package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.query.ZKQueryParam;
import cn.oyzh.easyzk.query.ZKQueryResult;
import cn.oyzh.fx.gui.tabs.RichTab;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryMsgTab extends RichTab {

    public ZKQueryMsgTab(ZKQueryParam param, ZKQueryResult result) {
        super();
        super.flush();
        this.controller().init(param, result);
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryMsgTab.fxml";
    }

    @Override
    protected ZKQueryMsgTabController controller() {
        return (ZKQueryMsgTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.message();
    }


}
