package cn.oyzh.easyzk.terminal.fourletterword;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024/11/29
 */
public class ZKSrvrCommandHandler extends ZKFourLetterWordCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final ZKFourLetterWordCommand furLetterWordCommand = new ZKSrvrCommand();

}
