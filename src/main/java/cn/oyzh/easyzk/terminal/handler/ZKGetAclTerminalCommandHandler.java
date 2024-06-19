package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.GetAclCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKGetAclTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new GetAclCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.GET_ACL);

    @Override
    public String commandName() {
        return "getAcl";
    }

    @Override
    public String commandArg() {
        return "[-s] path";
    }

    @Override
    public String commandDesc() {
        // return "获取权限";
        return I18nResourceBundle.i18nString("base.get", "base.acl");
    }

    @Override
    public String commandHelp(ZKTerminalTextArea terminal) {
        return super.commandHelp(terminal)  + "\n" +
                "-s stats";
    }
}
