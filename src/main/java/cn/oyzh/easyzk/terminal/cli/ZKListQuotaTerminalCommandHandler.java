package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.ListQuotaCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKListQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new ListQuotaCommand();

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
        return I18nResourceBundle.i18nString("base.iter", "base.quota");
    }
}
