package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CreateCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKCreateTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new CreateCommand();

    @Override
    public String commandName() {
        return "create";
    }

    @Override
    public String commandArg() {
        return "[-s] [-e] [-c] path [data] [acl]";
    }

    @Override
    public String commandDesc() {
        return "创建节点";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-s sequential\n" +
                "-e ephemeral\n" +
                "-c container";
    }
}
