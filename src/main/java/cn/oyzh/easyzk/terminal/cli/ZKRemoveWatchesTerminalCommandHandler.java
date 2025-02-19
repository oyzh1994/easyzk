package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.terminal.ZKTerminalTextAreaPane;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.RemoveWatchesCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKRemoveWatchesTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new RemoveWatchesCommand();

    @Override
    public String commandName() {
        return "removewatches";
    }

    @Override
    public String commandArg() {
        return "path [-c|-d|-a] [-l]";
    }

    @Override
    public String commandDesc() {
        return I18nResourceBundle.i18nString("base.remove", "base.watch");
    }

    @Override
    public String commandHelp(ZKTerminalTextAreaPane terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-c child watcher type\n" +
                "-d data watcher type\n" +
                "-a any watcher type\n" +
                "-l remove locally when there is no server connection";
    }
}
