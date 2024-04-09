package cn.oyzh.easyzk.tabs.changelog;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * zk更新日志表tab
 *
 * @author oyzh
 * @since 2024/04/07
 */
public class ChangelogTab extends DynamicTab {

    public ChangelogTab(){
        super();
        super.flush();
    }

    // @Override
    // public void flushTitle() {
    //     super.setTitle("更新日志");
    // }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/changelog.svg", "12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/changelog/changelogContent.fxml";
    }
}
