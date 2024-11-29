package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.AddAuthCommand;
import org.apache.zookeeper.cli.CliCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKAddAuthTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new AddAuthCommand();

    @Override
    public String commandName() {
        return "addauth";
    }

    @Override
    public String commandArg() {
        return "scheme auth";
    }

    @Override
    public String commandDesc() {
        // return "添加认证";
        return I18nResourceBundle.i18nString("base.add", "base.auth");
    }

}
