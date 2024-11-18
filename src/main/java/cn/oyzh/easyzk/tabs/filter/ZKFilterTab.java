package cn.oyzh.easyzk.tabs.filter;

import cn.oyzh.fx.gui.svg.glyph.FilterSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import javafx.scene.Cursor;

/**
 * zk过滤列表tab
 *
 * @author oyzh
 * @since 2023/11/03
 */
public class ZKFilterTab extends DynamicTab {

    public ZKFilterTab() {
        super();
        super.flush();
    }

    @Override
    public void flushGraphic() {
        FilterSVGGlyph glyph = (FilterSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new FilterSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/filter/zkFilterTabContent.fxml";
    }

    @Override
    public String getTabTitle() {
        return I18nResourceBundle.i18nString("base.title.filter.main");
    }
}
