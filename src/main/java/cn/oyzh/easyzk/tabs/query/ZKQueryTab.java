package cn.oyzh.easyzk.tabs.query;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKQuery;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.svg.glyph.ChangelogSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.changelog.Changelog;
import cn.oyzh.fx.plus.changelog.ChangelogListView;
import cn.oyzh.fx.plus.changelog.ChangelogManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * zk更新日志表tab
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ZKQueryTab extends DynamicTab {

    public ZKQueryTab() {
        super();
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
        return I18nHelper.query();
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
