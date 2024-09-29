package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.standard.ClearTerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager;
import cn.oyzh.fx.terminal.util.TerminalUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.LsCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKLs2TerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    // static {
    //     TerminalManager.registerHandler(ZKLs2TerminalCommandHandler.class);
    // }

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new LsCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.LS);

    @Override
    public TerminalCommand parseCommand(String line) {
        String[] args = TerminalUtil.split(line);
        TerminalCommand command = new TerminalCommand();
        List<String> list = new ArrayList<>();
        Collections.addAll(list, args);
        list.add(1, "-s");
        command.args(list.toArray(new String[]{}));
        return command;
    }

    @Override
    public String commandName() {
        return "ls2";
    }

    @Override
    public String commandArg() {
        return "path";
    }

    @Override
    public String commandDesc() {
        // return "获取子节点列表及状态";
        return I18nResourceBundle.i18nString("base.iter", "base.child");
    }

    @Override
    public boolean commandDeprecated() {
        return true;
    }
}
