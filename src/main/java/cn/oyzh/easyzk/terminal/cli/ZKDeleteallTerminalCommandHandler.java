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
import org.apache.zookeeper.cli.DeleteAllCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKDeleteallTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new DeleteAllCommand();

    @Override
    public String commandName() {
        return "deleteall";
    }

    @Override
    public String commandArg() {
        return "path";
    }

    @Override
    public String commandDesc() {
        return I18nResourceBundle.i18nString("base.delete", "base.node", "base.all");
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
        if (terminal.client().isReadonly()) {
            return TerminalExecuteResult.fail(new ReadonlyOperationException());
        }
        return super.execute(command, terminal);
    }
}
