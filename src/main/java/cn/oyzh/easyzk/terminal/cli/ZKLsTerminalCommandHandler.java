package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.LsCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKLsTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new LsCommand();

    @Override
    public String commandName() {
        return "ls";
    }

    @Override
    public String commandArg() {
        return "[-s] path";
    }

    @Override
    public String commandDesc() {
        return I18nResourceBundle.i18nString("base.iter", "base.child", "base.node");
    }

    @Override
    public String commandHelp(ZKTerminalTextTextArea terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-s stat";
    }
}
