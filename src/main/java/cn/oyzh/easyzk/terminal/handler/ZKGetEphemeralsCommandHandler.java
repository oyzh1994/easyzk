package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKPathTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.GetEphemeralsCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/12/21
 */
@Component
public class ZKGetEphemeralsCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new GetEphemeralsCommand();

    @Override
    public String commandName() {
        return "getEphemerals";
    }

    @Override
    public String commandArg() {
        return "path";
    }

    @Override
    public String commandDesc() {
        return "获取临时节点";
    }

    @Override
    public String commandSupportedVersion() {
        return "3.5.0";
    }
}
