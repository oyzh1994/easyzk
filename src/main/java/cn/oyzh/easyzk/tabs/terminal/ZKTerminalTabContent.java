package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.gui.tabs.DynamicTab;
import cn.oyzh.fx.gui.tabs.DynamicTabController;
import javafx.event.Event;
import javafx.fxml.FXML;
import lombok.NonNull;

/**
 * zk终端tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
public class ZKTerminalTabContent extends DynamicTabController {

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
    public ZKConnect info() {
        return this.terminal.info();
    }

    @Override
    public void onTabClose(DynamicTab tab, Event event) {
        ZKConnectUtil.close(this.client(), true);
        super.onTabClose(tab, event);
    }
}
