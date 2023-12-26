package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
// import org.apache.zookeeper.cli.CommandFactory;
import org.apache.zookeeper.cli.DeleteCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKDeleteTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand =new DeleteCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.DELETE);

    @Override
    public String commandName() {
        return "delete";
    }

    @Override
    public String commandArg() {
        return "[-v version] path";
    }

    @Override
    public String commandDesc() {
        return "删除节点";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-v version";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
        if (terminal.client().isReadonly()) {
            return TerminalExecuteResult.fail(new ReadonlyOperationException());
        }
        return super.execute(command, terminal);
    }
}
