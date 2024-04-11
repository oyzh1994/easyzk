package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.fx.plus.controls.svg.HomeSVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
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
        return "/tabs/home/zkHomeTabContent.fxml";
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
}
