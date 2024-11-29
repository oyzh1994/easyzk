package cn.oyzh.easyzk.terminal.basic;

import cn.oyzh.easyzk.terminal.ZKTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import cn.oyzh.fx.terminal.util.TerminalUtil;
import org.apache.zookeeper.cli.CliCommand;

/**
 * @author oyzh
 * @since 2023/7/21
 */
public abstract class ZKCliTerminalCommandHandler<C extends TerminalCommand> extends ZKTerminalCommandHandler<C> {

    protected abstract CliCommand cliCommand();

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
            ZKCliCommandWrapper wrapper = new ZKCliCommandWrapper(this.cliCommand(), terminal.zooKeeper());
            wrapper.parse(command.args());
            wrapper.setOnResponse(result::appendResult);
            wrapper.exec();
        } catch (Exception ex) {
            result.setException(ex);
        } finally {
            terminal.enable();
        }
        return result;
    }
}
