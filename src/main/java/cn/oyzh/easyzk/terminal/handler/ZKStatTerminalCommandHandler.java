package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.StatCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKStatTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new StatCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.STAT);

    @Override
    public String commandName() {
        return "stat";
    }

    @Override
    public String commandArg() {
        return "path";
    }

    @Override
    public String commandDesc() {
        // return "获取节点状态";
        return I18nResourceBundle.i18nString("base.get", "base.stat");
    }

}
