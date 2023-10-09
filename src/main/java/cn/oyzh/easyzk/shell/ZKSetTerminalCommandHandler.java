package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.SetCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKSetTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new SetCommand();

    @Override
    public String commandName() {
        return "set";
    }

    @Override
    public String commandArg() {
        return "[-s] [-v version] path data";
    }

    @Override
    public String commandDesc() {
        return "设置节点数据";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-s stats\n" +
                "-v version";
    }
}
