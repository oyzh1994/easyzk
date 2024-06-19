package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.terminal.ZKCliTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CloseCommand;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKCloseTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    @Getter(AccessLevel.PROTECTED)
    @Accessors(fluent = true)
    private final CliCommand cliCommand = new CloseCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.CLOSE);

    @Override
    public String commandName() {
        return "close";
    }

    @Override
    public String commandDesc() {
        // return "关闭连接";
        return I18nResourceBundle.i18nString("base.close", "base.connect");
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
        try {
            return super.execute(command, terminal);
        } finally {
            ZKEventUtil.terminalClose(terminal.info());
        }
    }
}
