package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CommandFactory;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKVersionTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.VERSION);

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 1;
    }

    // @Override
    // public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
    //     TerminalExecuteResult result = new TerminalExecuteResult();
    //     try {
    //         result.setResult(Version.getFullVersion());
    //     } catch (Exception ex) {
    //         ex.printStackTrace();
    //         result.setException(ex);
    //     }
    //     return result;
    // }

    @Override
    public String commandName() {
        return "version";
    }

    @Override
    public String commandDesc() {
        return "获取客户端版本";
    }

}
