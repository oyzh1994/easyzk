package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CreateCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKCreateTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    // static {
    //     TerminalManager.registerHandler(ZKCreateTerminalCommandHandler.class);
    // }

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new CreateCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.CREATE);

    @Override
    public String commandName() {
        return "create";
    }

    @Override
    public String commandArg() {
        return "[-s] [-e] [-c] path [data] [acl]";
    }

    @Override
    public String commandDesc() {
        // return "创建节点";
        return I18nResourceBundle.i18nString("base.create", "base.node");
    }

    @Override
    public String commandHelp(ZKTerminalTextTextArea terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-s sequential\n" +
                "-e ephemeral\n" +
                "-c container";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextTextArea terminal) {
        if (terminal.client().isReadonly()) {
            return TerminalExecuteResult.fail(new ReadonlyOperationException());
        }
        return super.execute(command, terminal);
    }
}
