package cn.oyzh.easyzk.tabs.filter;

import cn.oyzh.fx.plus.controls.svg.FilterSVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
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

    // @Override
    // public void flushTitle() {
    //     super.setTitle("过滤配置列表");
    // }

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

}
