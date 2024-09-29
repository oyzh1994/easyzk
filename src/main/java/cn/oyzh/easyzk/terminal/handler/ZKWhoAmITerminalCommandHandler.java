package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.WhoAmICommand;

/**
 * @author oyzh
 * @since 2023/12/21
 */
// @Component
public class ZKWhoAmITerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    // static {
    //     TerminalManager.registerHandler(ZKWhoAmITerminalCommandHandler.class);
    // }

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new WhoAmICommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.WHO_AM_I);

    @Override
    public String commandName() {
        return "whoami";
    }

    @Override
    public String commandDesc() {
        // return "获取连接用户信息";
        return I18nResourceBundle.i18nString("base.get", "base.connected", "base.userInfo");
    }
}
