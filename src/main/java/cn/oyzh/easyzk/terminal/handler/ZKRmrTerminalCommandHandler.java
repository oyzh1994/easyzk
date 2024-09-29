package cn.oyzh.easyzk.terminal.handler;

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
