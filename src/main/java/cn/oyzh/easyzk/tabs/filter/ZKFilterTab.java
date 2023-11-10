package cn.oyzh.easyzk.tabs.filter;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * zk过滤列表tab
 *
 * @author oyzh
 * @since 2023/11/03
 */
public class ZKFilterTab extends DynamicTab {

    /**
     * 执行初始化
     */
    public void init() {
        this.flushGraphic();
        this.flushTitle();
    }

    @Override
    public void flushTitle() {
        super.title("过滤配置列表");
    }

    @Override
    public void flushGraphic() {
        SVGGlyph glyph = (SVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new SVGGlyph("/font/filter.svg", "12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    @Override
    protected String url() {
        return "/tabs/filter/zkFilterTabContent.fxml";
    }
}
