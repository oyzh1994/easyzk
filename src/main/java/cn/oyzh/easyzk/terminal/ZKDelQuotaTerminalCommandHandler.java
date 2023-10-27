package cn.oyzh.easyzk.terminal;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.DelQuotaCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKDelQuotaTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new DelQuotaCommand();

    @Override
    public String commandName() {
        return "delquota";
    }

    @Override
    public String commandArg() {
        return "[-n|-b] path";
    }

    @Override
    public String commandDesc() {
        return "删除配额";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-n num quota\n" +
                "-b bytes quota";
    }
}
