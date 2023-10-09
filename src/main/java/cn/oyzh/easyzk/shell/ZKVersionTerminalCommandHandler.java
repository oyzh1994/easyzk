package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import org.apache.zookeeper.Version;
import org.springframework.stereotype.Component;

/**
 * @author oyzh
 * @since 2023/09/20
 */
@Component
public class ZKVersionTerminalCommandHandler extends ZKTerminalCommandHandler<TerminalCommand> {

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 1;
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            result.setResult(Version.getFullVersion());
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setException(ex);
        }
        return result;
    }

    @Override
    public String commandName() {
        return "version";
    }

    @Override
    public String commandDesc() {
        return "获取客户端版本";
    }

}
