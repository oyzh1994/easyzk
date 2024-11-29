package cn.oyzh.easyzk.terminal.cli;

import cn.oyzh.easyzk.terminal.ZKTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKConnectTerminalCommandHandler extends ZKTerminalCommandHandler<TerminalCommand> {

    // static {
    //     TerminalManager.registerHandler(ZKConnectTerminalCommandHandler.class);
    // }

    @Override
    protected TerminalCommand parseCommand(String line, String[] args) {
        TerminalCommand terminalCommand = new TerminalCommand();
        terminalCommand.args(args);
        terminalCommand.command(line);
        return terminalCommand;
    }

    @Override
    protected boolean checkArgs(String[] args) throws RuntimeException {
        return args != null && args.length >= 2 && args.length <= 6;
    }

    @Override
    public String commandName() {
        return "connect";
    }

    @Override
    public String commandDesc() {
        // return "开始连接";
        return I18nResourceBundle.i18nString("base.startConnect");
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextTextArea terminal) {
        if (terminal.isTemporary()) {
            if (terminal.isConnected()) {
                terminal.client().close();
            }
            terminal.connect(command.command());
        } else {
            terminal.outputByPrompt("非临时连接不支持此操作");
        }
        TerminalExecuteResult result = TerminalExecuteResult.ok();
        result.setIgnoreOutput(true);
        return result;
    }
}
