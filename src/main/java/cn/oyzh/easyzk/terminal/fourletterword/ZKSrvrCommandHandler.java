package cn.oyzh.easyzk.terminal.fourletterword;

import cn.oyzh.fx.terminal.command.TerminalCommand;

/**
 * @author oyzh
 * @since 2024/11/29
 */
public class ZKSrvrCommandHandler extends ZKFourLetterWordCommandHandler<TerminalCommand> {

    private final ZKFourLetterWordCommand furLetterWordCommand = new ZKSrvrCommand();

    @Override
    public ZKFourLetterWordCommand furLetterWordCommand() {
        return this.furLetterWordCommand;
    }

}
