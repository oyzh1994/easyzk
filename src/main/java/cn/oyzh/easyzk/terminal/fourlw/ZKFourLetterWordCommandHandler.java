package cn.oyzh.easyzk.terminal.fourlw;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.terminal.ZKTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import cn.oyzh.fx.terminal.util.TerminalUtil;

/**
 * @author oyzh
 * @since 2023/7/21
 */
public abstract class ZKFourLetterWordCommandHandler<C extends TerminalCommand> extends ZKTerminalCommandHandler<C> {

    protected abstract ZKFourLetterWordCommand furLetterWordCommand();

    @Override
    public C parseCommand(String line) {
        String[] args = TerminalUtil.split(line);
        TerminalCommand command = new TerminalCommand();
        command.args(args);
        return (C) command;
    }

    @Override
    public TerminalExecuteResult execute(C command, ZKTerminalTextTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            terminal.disable();
            ZKConnect connect = terminal.connect();
            String execResult = this.furLetterWordCommand().exec(connect.hostIp(), connect.hostPort());
            result.setResult(execResult);
        } catch (Exception ex) {
            result.setException(ex);
        } finally {
            terminal.enable();
        }
        return result;
    }
}
