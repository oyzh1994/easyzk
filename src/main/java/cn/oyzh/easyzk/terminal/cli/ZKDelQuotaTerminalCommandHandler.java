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
import org.apache.zookeeper.cli.DelQuotaCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKDelQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new DelQuotaCommand();

    @Override
    public String commandName() {
        return "delquota";
    }

    @Override
    public String commandArg() {
        return "[-n|-b] path";
    }

    @Override
    public String commandDesc() {
        // return "删除配额";
        return I18nResourceBundle.i18nString("base.delete", "base.quota");
    }

    @Override
    public String commandHelp(ZKTerminalTextArea terminal) {
        return super.commandHelp(terminal)  + "\n" +
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
