package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.fx.gui.svg.glyph.HomeSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.Cursor;

/**
 * zk主页tab
 *
 * @author oyzh
 * @since 2023/5/24
 */
public class ZKHomeTab extends DynamicTab {

    public ZKHomeTab() {
        super();
        super.flush();
    }

    @Override
    protected String url() {
        return "/tabs/home/zkHomeTab.fxml";
    }

    @Override
    public void flushGraphic() {
        HomeSVGGlyph glyph = (HomeSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new HomeSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    public String getTabTitle() {
        return I18nHelper.homeTitle();
    }

}
