package cn.oyzh.easyzk.terminal;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.SetAclCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKSetAclTerminalCommandHandler extends ZKPathTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new SetAclCommand();

    @Override
    public String commandName() {
        return "setAcl";
    }

    @Override
    public String commandArg() {
        return "[-s] [-v version] path acl";
    }

    @Override
    public String commandDesc() {
        return "设置权限";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-s stats\n" +
                "-v version";
    }
}
