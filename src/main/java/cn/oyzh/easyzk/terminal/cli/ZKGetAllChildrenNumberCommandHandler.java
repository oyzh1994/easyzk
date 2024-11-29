package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.GetAllChildrenNumberCommand;

/**
 * @author oyzh
 * @since 2023/12/21
 */
// @Component
public class ZKGetAllChildrenNumberCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    // static {
    //     TerminalManager.registerHandler(ZKGetAllChildrenNumberCommandHandler.class);
    // }

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new GetAllChildrenNumberCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.GET_ALL_CHILDREN_NUMBER);

    @Override
    public String commandName() {
        return "getAllChildrenNumber";
    }

    @Override
    public String commandArg() {
        return "path";
    }

    @Override
    public String commandDesc() {
        // return "获取全部子节点数量";
        return I18nResourceBundle.i18nString("base.get", "base.all", "base.childNumber");
    }

    @Override
    public String commandSupportedVersion() {
        return "3.5.0";
    }
}
