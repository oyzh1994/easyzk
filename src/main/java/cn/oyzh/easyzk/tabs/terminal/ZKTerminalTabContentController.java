package cn.oyzh.easyzk.tabs.terminal;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.spring.ScopeType;
import javafx.fxml.FXML;
import lombok.NonNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * redis终端tab内容组件
 *
 * @author oyzh
 * @since 2023/07/21
 */
@Lazy
@Component
@Scope(ScopeType.PROTOTYPE)
public class ZKTerminalTabContentController {

    /**
     * redis命令行文本域
     */
    @FXML
    private ZKTerminalTextArea terminal;

    /**
     * 设置redis客户端
     *
     * @param client redis客户端
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
    public ZKInfo info() {
        return this.terminal.info();
    }
}
