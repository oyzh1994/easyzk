package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.SetQuotaCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKSetQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new SetQuotaCommand();

    @Override
    public String commandName() {
        return "setquota";
    }

    @Override
    public String commandArg() {
        return "-n|-b val path";
    }

    @Override
    public String commandDesc() {
        return "设置配额";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-n num quota\n" +
                "-b bytes quota";
    }
}
