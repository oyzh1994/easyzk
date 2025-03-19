package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.terminal.ZKTerminalTextAreaPane;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.GetConfigCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKGetConfigTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    private final CliCommand cliCommand = new GetConfigCommand();

    @Override
    public CliCommand cliCommand() {
        return this.cliCommand;
    }

    @Override
    public String commandName() {
        return "config";
    }

    @Override
    public String commandArg() {
        return "[-c] [-s]";
    }

    @Override
    public String commandDesc() {
        return I18nResourceBundle.i18nString("base.get", "base.config");
    }

    @Override
    public String commandHelp(ZKTerminalTextAreaPane terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-c client connection string\n" +
                "-s stats";
    }
}
