package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.AddAuthCommand;
import org.apache.zookeeper.cli.CliCommand;
// import org.apache.zookeeper.cli.CommandFactory;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKAddAuthTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new AddAuthCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.ADD_AUTH);

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
        return "添加认证";
    }

}
