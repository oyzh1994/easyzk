package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.ReconfigCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKReconfigTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand zkCommand = new ReconfigCommand();

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
        return "重新配置";
    }

    @Override
    public String commandHelp() {
        return super.commandHelp() + "\n" +
                "-s stats\n" +
                "-v required current config version\n" +
                "-file path of config file to parse for membership\n" +
                "-members comma-separated list of config strings for non-incremental reconfig\n" +
                "-add comma-separated list of config strings for new servers\n" +
                "-remove comma-separated list of server IDs to remove";
    }
}
