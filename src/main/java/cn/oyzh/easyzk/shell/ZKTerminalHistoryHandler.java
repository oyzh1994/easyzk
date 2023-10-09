package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.histroy.BaseTerminalHistoryHandler;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class ZKTerminalHistoryHandler extends BaseTerminalHistoryHandler {

    /**
     * 当前实例
     */
    public static final ZKTerminalHistoryHandler INSTANCE = new ZKTerminalHistoryHandler();

    public ZKTerminalHistoryHandler() {
        super(ZKTerminalHistoryStore.INSTANCE);
    }
}
