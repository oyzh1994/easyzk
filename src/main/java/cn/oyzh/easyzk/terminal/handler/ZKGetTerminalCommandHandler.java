package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.GetCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKGetTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new GetCommand();

    @Override
    public String commandName() {
        return "get";
    }

    @Override
    public String commandArg() {
        return "[-s] path";
    }

    @Override
    public String commandDesc() {
        return "获取节点数据及状态";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-s stat";
    }
}
