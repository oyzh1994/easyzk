package cn.oyzh.easyzk.terminal.basic;

import cn.oyzh.easyzk.exception.ReadonlyOperationException;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.ReconfigCommand;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKReconfigTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    // static {
    //     TerminalManager.registerHandler(ZKReconfigTerminalCommandHandler.class);
    // }

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new ReconfigCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.RECONFIG);

    @Override
    public String commandName() {
        return "reconfig";
    }

    @Override
    public String commandArg() {
        return "[-s] [-v version] [[-file path] | [-members serverID=host:port1:port2;port3[,...]*]] | [-add serverId=host:port1:port2;port3[,...]]* [-remove serverId[,...]*]";
    }

    @Override
    public String commandDesc() {
        // return "重新配置";
        return I18nResourceBundle.i18nString("base.re", "base.config");
    }

    @Override
    public String commandHelp(ZKTerminalTextTextArea terminal) {
        return super.commandHelp(terminal) + "\n" +
                "-s stats\n" +
                "-v required current config version\n" +
                "-file path of config file to parse for membership\n" +
                "-members comma-separated list of config strings for non-incremental reconfig\n" +
                "-add comma-separated list of config strings for new servers\n" +
                "-remove comma-separated list of server IDs to remove";
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextTextArea terminal) {
        if (terminal.client().isReadonly()) {
            return TerminalExecuteResult.fail(new ReadonlyOperationException());
        }
        return super.execute(command, terminal);
    }
}
