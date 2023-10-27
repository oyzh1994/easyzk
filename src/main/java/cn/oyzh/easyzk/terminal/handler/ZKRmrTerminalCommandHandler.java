package cn.oyzh.easyzk.terminal.handler;

import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKRmrTerminalCommandHandler extends ZKDeleteallTerminalCommandHandler {

    @Override
    public String commandName() {
        return "rmr";
    }
}
