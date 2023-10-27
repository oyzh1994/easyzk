package cn.oyzh.easyzk.terminal;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CloseCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKCloseTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new CloseCommand();

    @Override
    public String commandName() {
        return "close";
    }

    @Override
    public String commandDesc() {
        return "关闭连接";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
        try {
            return super.execute(command, terminal);
        } finally {
            ZKEventUtil.closeTerminal(terminal.info());
        }
    }
}
