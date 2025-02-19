package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.terminal.ZKTerminalTextAreaPane;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.GetAclCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
public class ZKGetAclTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new GetAclCommand();

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
    public String commandHelp(ZKTerminalTextAreaPane terminal) {
        return super.commandHelp(terminal)  + "\n" +
                "-s stats";
    }
}
