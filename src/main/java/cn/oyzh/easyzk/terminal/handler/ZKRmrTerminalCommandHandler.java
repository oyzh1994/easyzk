package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.fx.terminal.util.TerminalManager;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKRmrTerminalCommandHandler extends ZKDeleteallTerminalCommandHandler {

    // static {
    //     TerminalManager.registerHandler(ZKRmrTerminalCommandHandler.class);
    // }

    @Override
    public String commandName() {
        return "rmr";
    }

    @Override
    public boolean commandDeprecated() {
        return true;
    }
}
