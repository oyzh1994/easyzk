package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CommandFactory;
import org.apache.zookeeper.cli.LsCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKLsTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    // private final CliCommand cliCommand = new LsCommand();
    private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.LS);

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
        // return "获取子节点列表";
        return I18nResourceBundle.i18nString("base.iter", "base.child");
    }

    @Override
    public String commandHelp(ZKTerminalTextArea terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-s stat";
    }
}
