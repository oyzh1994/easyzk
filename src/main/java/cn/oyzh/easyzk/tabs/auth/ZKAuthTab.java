package cn.oyzh.easyzk.tabs.auth;

import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.svg.SVGLabel;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.scene.Cursor;

/**
 * zk认证tab
 *
 * @author oyzh
 * @since 2023/11/03
 */
public class ZKAuthTab extends DynamicTab {

    /**
     * 执行初始化
     */
    public void init() {
        this.flushGraphic();
        this.flushTitle();
    }

    @Override
    public void flushTitle() {
        SVGLabel graphic = (SVGLabel) this.getGraphic();
        // 设置文本
        graphic.setText("zk认证信息列表");
        // 设置提示文本
        this.setTipText("zk认证信息列表");
    }

    @Override
    public void flushGraphic() {
        SVGLabel label = (SVGLabel) this.getGraphic();
        if (label == null) {
            SVGLabel graphic1 = new SVGLabel(null, new SVGGlyph("/font/audit.svg", "12"));
            graphic1.setCursor(Cursor.DEFAULT);
            FXUtil.runWait(() -> this.setGraphic(graphic1));
        }
    }

    @Override
    protected String url() {
        return "/tabs/auth/zkAuthTabContent.fxml";
    }
}
