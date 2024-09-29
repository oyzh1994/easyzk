package cn.oyzh.easyzk.terminal.handler;

import cn.oyzh.easyzk.terminal.ZKTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.ZKTerminalTextTextArea;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.command.TerminalCommand;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import cn.oyzh.fx.terminal.util.TerminalManager;
import org.apache.zookeeper.Version;

/**
 * @author oyzh
 * @since 2023/09/20
 */
// @Component
public class ZKVersionTerminalCommandHandler extends ZKTerminalCommandHandler<TerminalCommand> {
// public class ZKVersionTerminalCommandHandler extends ZKCliTerminalCommandHandler<TerminalCommand> {

    static {
        TerminalManager.registerHandler(ZKVersionTerminalCommandHandler.class);
    }

    // @Getter(AccessLevel.PROTECTED)
    // @Accessors(fluent = true)
    // private final CliCommand cliCommand = new VersionCommand();
    // private final CliCommand cliCommand = CommandFactory.getInstance(CommandFactory.Command.VERSION);

    @Override
    protected boolean checkArgs(String[] words) {
        return words.length == 1;
    }

    @Override
    public TerminalExecuteResult execute(TerminalCommand command, ZKTerminalTextTextArea terminal) {
        TerminalExecuteResult result = new TerminalExecuteResult();
        try {
            result.setResult("ZooKeeper CLI version: " + Version.getFullVersion());
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
        // return "获取客户端版本";
        return I18nResourceBundle.i18nString("base.get", "base.client", "base.version");
    }

}
