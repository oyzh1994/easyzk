package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
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

    public ZKTerminalTab(ZKClient client) {
        this.init(client);
    }
//
//    public ZKTerminalTab(ZKConnect connect) {
//        this.init(connect);
//    }

    @Override
    public void flushGraphic() {
        TerminalSVGGlyph glyph = (TerminalSVGGlyph) this.getGraphic();
        if (glyph == null) {
            glyph = new TerminalSVGGlyph("12");
            glyph.setCursor(Cursor.DEFAULT);
            this.graphic(glyph);
        }
    }

//    /**
//     * 初始化
//     *
//     * @param zkConnect zk连接
//     */
//    public void init(ZKConnect zkConnect) {
//        try {
//            if (zkConnect == null) {
//                zkConnect = new ZKConnect();
//                zkConnect.setName(I18nHelper.unnamedConnection());
//            }
//            // 刷新图标
//            this.flushGraphic();
//            // 设置标题
//            super.setTitle(zkConnect.getName());
//            // 初始化zk连接
//            this.controller().client(new ZKClient(zkConnect));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    /**
     * 初始化
     *
     * @param client zk客户端
     */
    public void init(ZKClient client) {
        try {
            if (client == null) {
                ZKConnect connect = new ZKConnect();
                connect.setName(I18nHelper.unnamedConnection());
                // 刷新图标
                this.flushGraphic();
                // 设置标题
                super.setTitle(connect.getName());
                // 初始化zk连接
                this.controller().client(new ZKClient(connect));
            } else {
                // 刷新图标
                this.flushGraphic();
                // 设置标题
                super.setTitle(client.zkConnect().getName());
                // 初始化zk连接
                this.controller().client(client);
            }
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
        return "/tabs/terminal/zkTerminalTab.fxml";
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
        private ZKTerminalTextArea terminal;

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
            if (this.terminal.isTemporary()) {
                ZKConnectUtil.close(this.client(), true, true);
            }
            super.onTabClose(tab, event);
        }
    }
}
