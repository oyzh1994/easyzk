package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.SetCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKSetTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new SetCommand();

    @Override
    public String commandName() {
        return "set";
    }

    @Override
    public String commandArg() {
        return "[-s] [-v version] path data";
    }

    @Override
    public String commandDesc() {
        return I18nResourceBundle.i18nString("base.set", "base.data");
    }

    @Override
    public String commandHelp(ZKTerminalTextArea terminal) {
        return super.commandHelp(terminal)  + "\n" +
                "-s stats\n" +
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
