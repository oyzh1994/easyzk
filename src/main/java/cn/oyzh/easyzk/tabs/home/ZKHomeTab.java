package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * zk主页tab
 *
 * @author oyzh
 * @since 2023/5/24
 */
public class ZKHomeTab extends DynamicTab {

    /**
     * 执行初始化
     */
    public void init() {
        this.flushGraphic();
        this.flushTitle();
    }

    @Override
    protected String url() {
        return "/tabs/home/zkHomeTabContent.fxml";
    }

    @Override
    public void flushTitle() {
        super.title("主页");
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/home.svg", "12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }
}
