package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.SetQuotaCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKSetQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new SetQuotaCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.SET_QUOTA);

    @Override
    public String commandName() {
        return "setquota";
    }

    @Override
    public String commandArg() {
        return "-n|-b val path";
    }

    @Override
    public String commandDesc() {
        return "设置配额";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-n num quota\n" +
                "-b bytes quota";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
        if (terminal.client().isReadonly()) {
            return TerminalExecuteResult.fail(new ReadonlyOperationException());
        }
        return super.execute(command, terminal);
    }
}
