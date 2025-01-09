package cn.oyzh.easyzk.tabs.server;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.svg.glyph.ServerSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.scene.Cursor;

/**
 * redis服务信息tab
 *
 * @author oyzh
 * @since 2023/08/01
 */
public class ZKServerTab extends DynamicTab {

    @Override
    public ZKServerTabController controller() {
        return (ZKServerTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/server/zkServerTab.fxml";
    }

    @Override
    public void flushGraphic() {
        SVGGlyph graphic = (SVGGlyph) this.getGraphic();
        if (graphic == null) {
            graphic = new ServerSVGGlyph("13");
            graphic.setCursor(Cursor.DEFAULT);
            this.setGraphic(graphic);
        }
    }

    /**
     * 初始化
     *
     * @param client redis客户端
     */
    public void init(ZKClient client) {
        try {
            // 设置文本
            this.setText(I18nHelper.serverInfo() + "-" + client.connectName());
            // 刷新图标
            this.flushGraphic();
            // 初始化
            this.controller().init(client);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 关闭刷新任务
     */
    public void closeRefreshTask() {
        this.controller().closeRefreshTask();
    }

    /**
     * redis信息
     *
     * @return redis信息
     */
    public ZKConnect zkConnect() {
        return this.controller().client().zkConnect();
    }

    /**
     * redis客户端
     *
     * @return redis客户端
     */
    public ZKClient client() {
        return this.controller().client();
    }

    @Override
    protected void onTabClosed(Event event) {
        super.onTabClosed(event);
        this.closeRefreshTask();
    }

    @Override
    protected void onTabCloseRequest(Event event) {
        super.onTabCloseRequest(event);
        this.closeRefreshTask();
    }


}
