package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.terminal.ZKTerminalTextAreaPane;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.SetQuotaCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKSetQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new SetQuotaCommand();

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
        return I18nResourceBundle.i18nString("base.set", "base.quota");
    }

    @Override
    public String commandHelp(ZKTerminalTextAreaPane terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-n num quota\n" +
                "-b bytes quota";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextAreaPane terminal) {
        if (terminal.client().isReadonly()) {
            return TerminalExecuteResult.fail(new ReadonlyOperationException());
        }
        return super.execute(command, terminal);
    }
}
