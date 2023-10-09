package cn.oyzh.easyzk.tabs.home;

import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.view.FXMLLoaderExt;
import cn.oyzh.easyzk.tabs.ZKBaseTab;
import javafx.scene.CacheHint;
import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * zk主页tab
 *
 * @author oyzh
 * @since 2023/5/24
 */
public class ZKHomeTab extends ZKBaseTab {

    @Override
    protected void loadContent() {
        FXMLLoaderExt loaderExt = new FXMLLoaderExt();
        Node content = loaderExt.load("/tabs/home/zkHomeTabContent.fxml");
        content.setCache(true);
        content.setCacheHint(CacheHint.QUALITY);
        this.setContent(content);
        this.setText("主页");
        // 刷新图标
        this.flushGraphic();
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/home.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }
}
