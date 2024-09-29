package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.controls.svg.TerminalSVGGlyph;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * zk终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class ZKTerminalTab extends DynamicTab {

    @Override
    public void flushGraphic() {
        TerminalSVGGlyph glyph = (TerminalSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new TerminalSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

    /**
     * 初始化
     *
     * @param info zk信息
     */
    public void init(ZKInfo info) {
        try {
            if (info == null) {
                info = new ZKInfo();
                info.setName(I18nHelper.unnamedConnection());
            }
            // 刷新图标
            this.flushGraphic();
            // 设置标题
            super.setTitle(info.getName());
            // 初始化zk连接
            this.controller().client(new ZKClient(info));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.controller().client();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKInfo info() {
        return this.controller().info();
    }

    @Override
    public ZKTerminalTabContent controller() {
        return (ZKTerminalTabContent) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/terminal/zkTerminalTabContent.fxml";
    }
}
