package cn.oyzh.easyzk.tabs.auth;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * zk认证列表tab
 *
 * @author oyzh
 * @since 2023/11/03
 */
public class ZKAuthTab extends DynamicTab {

    public ZKAuthTab(){
        super();
        super.flush();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/audit.svg", "12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/auth/zkAuthTabContent.fxml";
    }

    @Override
    public String getTabTitle() {
        return I18nResourceBundle.i18nString("zk.title.auth.main");
    }
}
