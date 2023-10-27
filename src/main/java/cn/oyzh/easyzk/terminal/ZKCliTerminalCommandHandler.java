package cn.oyzh.easyzk.terminal;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import cn.oyzh.fx.terminal.util.TerminalUtil;
import org.apache.zookeeper.cli.CliCommand;

/**
 * @author oyzh
 * @since 2023/7/21
 */
public abstract class ZKCliTerminalCommandHandler<C extends TerminalCommand> extends ZKTerminalCommandHandler<C> {

    protected abstract CliCommand zkCommand();

    @Override
    public C parseCommand(String line) {
        String[] args = TerminalUtil.split(line);
        TerminalCommand command = new TerminalCommand();
        command.args(args);
        return (C) command;
    }

    @Override
    public TerminalExecuteResult execute(C command, ZKTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            terminal.disable();
            ZKCliCommandWrapper wrapper = new ZKCliCommandWrapper(this.zkCommand());
            wrapper.parse(command.args());
            wrapper.init(terminal.zooKeeper());
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
