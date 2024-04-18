package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.RemoveWatchesCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKRemoveWatchesTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new RemoveWatchesCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.REMOVE_WATCHES);

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
        // return "移除订阅";
        return I18nResourceBundle.i18nString("base.remove", "base.watch");
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-c child watcher type\n" +
                "-d data watcher type\n" +
                "-a any watcher type\n" +
                "-l remove locally when there is no server connection";
    }
}
