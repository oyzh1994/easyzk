package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.tabs.DynamicTab;
import javafx.scene.Cursor;

/**
 * redis终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class ZKTerminalTab extends DynamicTab {

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new SVGGlyph("/font/code library.svg", "13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
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
                info.setName("未命名连接");
            }
            // 设置文本
            this.setText(info.getName());
            // 刷新图标
            this.flushGraphic();
            // 初始化redis连接
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
        return "/tabs/node/zkTerminalTabContent.fxml";
    }
}
