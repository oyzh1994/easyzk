package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.util.FXUtil;
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
        this.setText("主页");
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/home.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            SVGGlyph finalGraphic = graphic;
            FXUtil.runWait(() -> this.setGraphic(finalGraphic));
        }
    }
}
