package cn.oyzh.easyzk.tabs;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import cn.oyzh.i18n.I18nHelper;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import lombok.NonNull;

/**
 * zk终端tab
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class ZKTerminalTab extends DynamicTab {

    public ZKTerminalTab(ZKConnect zkInfo) {
        this.init(zkInfo);
    }

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
    public void init(ZKConnect info) {
        try {
            if (info == null) {
                info = new ZKConnect();
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
    public ZKConnect connect() {
        return this.controller().connect();
    }

    @Override
    public ZKTerminalTabController controller() {
        return (ZKTerminalTabController) super.controller();
    }

    @Override
    protected String url() {
        return "/tabs/zkTerminalTab.fxml";
    }

    /**
     * zk终端tab内容组件
     *
     * @author oyzh
     * @since 2023/07/21
     */
    public static class ZKTerminalTabController extends DynamicTabController {

        /**
         * 命令行文本域
         */
        @FXML
        private ZKTerminalTextTextArea terminal;

        /**
         * 设置客户端
         *
         * @param client 客户端
         */
        public void client(@NonNull ZKClient client) {
            this.terminal.init(client);
        }

        /**
         * 获取zk客户端
         *
         * @return zk客户端
         */
        public ZKClient client() {
            return this.terminal.client();
        }

        /**
         * 获取zk信息
         *
         * @return zk信息
         */
        public ZKConnect connect() {
            return this.terminal.connect();
        }

        @Override
        public void onTabClose(DynamicTab tab, Event event) {
            ZKConnectUtil.close(this.client(), true);
            super.onTabClose(tab, event);
        }
    }
}
