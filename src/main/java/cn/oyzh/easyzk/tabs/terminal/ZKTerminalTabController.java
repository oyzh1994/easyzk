package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.terminal.ZKTerminalPane;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.RichTabController;
import javafx.event.Event;
import javafx.fxml.FXML;

/**
 * zk终端tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class ZKTerminalTabController extends RichTabController {

    /**
     * 命令行文本域
     */
    @FXML
    private ZKTerminalPane terminal;

    /**
     * 设置客户端
     *
     * @param client 客户端
     */
    public void client( ZKClient client) {
        this.terminal.init(client);
    }

    /**
     * 获取zk客户端
     *
     * @return zk客户端
     */
    public ZKClient client() {
        return this.terminal.getClient();
    }

    /**
     * 获取zk信息
     *
     * @return zk信息
     */
    public ZKConnect zkConnect() {
        return this.terminal.zkConnect();
    }

    @Override
    public void onTabClosed(Event event) {
        if (this.terminal.isTemporary()) {
            ZKConnectUtil.close(this.client(), true, true);
        }
        super.onTabClosed(event);
    }
}
