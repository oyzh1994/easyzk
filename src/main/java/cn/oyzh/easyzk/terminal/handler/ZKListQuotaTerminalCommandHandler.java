package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.standard.ClearTerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.ListQuotaCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKListQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    static {
        TerminalManager.registerHandler(ZKListQuotaTerminalCommandHandler.class);
    }

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new ListQuotaCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.LIST_QUOTA);

    @Override
    public String commandName() {
        return "listquota";
    }

    @Override
    public String commandArg() {
        return "path";
    }

    @Override
    public String commandDesc() {
        // return "列举配额";
        return I18nResourceBundle.i18nString("base.iter", "base.quota");
    }
}
