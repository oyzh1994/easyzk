package cn.oyzh.easyzk.tabs;

import cn.oyzh.fx.gui.svg.glyph.ChangelogSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.fx.plus.changelog.Changelog;
import cn.oyzh.fx.plus.changelog.ChangelogListView;
import cn.oyzh.fx.plus.changelog.ChangelogManager;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
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
public class ZKChangelogTab extends DynamicTab {

    public ZKChangelogTab() {
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
        return "/tabs/changelog/changelogContent.fxml";
    }

    @Override
    public String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.changelog");
    }

    /**
     * zk更新日志tab内容组件
     *
     * @author oyzh
     * @since 2024/04/07
     */
    public static class ChangelogTabContent extends DynamicTabController {

        /**
         * 更新日志
         */
        @FXML
        private ChangelogListView changelog;

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            super.initialize(url, resourceBundle);
            // 更新日志列表
            List<Changelog> changelogs = ChangelogManager.load();
            // 初始化更新日志
            this.changelog.init(changelogs);
        }
    }
}
