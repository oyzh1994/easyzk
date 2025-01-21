package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.svg.glyph.ChangelogSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * @author oyzh
 * @since 2025/01/20
 */
public class ZKQueryTab extends DynamicTab {

    public ZKQueryTab(ZKClient client, ZKQuery query) {
        super();
        this.init(client, query);
        super.flush();
    }

    @Override
    public void flushGraphic() {
        ChangelogSVGGlyph glyph = (ChangelogSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new ChangelogSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/query/zkQueryTab.fxml";
    }

    @Override
    protected ZKQueryTabController controller() {
        return (ZKQueryTabController) super.controller();
    }

    @Override
    public String getTabTitle() {
        if (this.controller().isUnsaved()) {
            return this.query().getName() + " *";
        }
        return this.query().getName();
    }

    public ZKQuery query() {
        return this.controller().getQuery();
    }

    public ZKConnect zkConnect() {
        return this.controller().zkConnect();
    }

    public void init(ZKClient client) {
        this.controller().init(client, null);
    }

    public void init(ZKClient client, ZKQuery query) {
        this.controller().init(client, query);
    }
}
