package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.command.TerminalCommandHandler;
import cn.oyzh.fx.terminal.complete.BaseTerminalCompleteHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;

import java.util.List;

/**
 * redis终端提示器
 *
 * @author oyzh
 * @since 2023/7/24
 */
public class ZKTerminalCompleteHandler extends BaseTerminalCompleteHandler {

    protected List<TerminalCommandHandler> findCommandHandlers(String line) {
        if (line.contains(" /")) {
            return TerminalManager.findHandlers(line.split(" ")[0], 2);
        }
        return super.findCommandHandlers(line.split(" ")[0]);
    }

    /**
     * 当前实例
     */
    public static final ZKTerminalCompleteHandler INSTANCE = new ZKTerminalCompleteHandler();

}
