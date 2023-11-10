package cn.oyzh.easyzk.terminal;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.util.SpringUtil;
import cn.oyzh.fx.terminal.execute.TerminalExecuteResult;
import cn.oyzh.fx.terminal.key.TerminalKeyHandler;
import cn.oyzh.fx.terminal.standard.HelpTerminalCommandHandler;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class ZKTerminalKeyHandler implements TerminalKeyHandler<ZKTerminalTextArea> {

    /**
     * 当前实例
     */
    public static final ZKTerminalKeyHandler INSTANCE = new ZKTerminalKeyHandler();

    @Override
    public boolean onEnterKeyPressed(ZKTerminalTextArea terminal) throws Exception {
        String input = terminal.getInput();
        if (terminal.isTemporary() && !terminal.isConnected()) {
            terminal.connect(input);
            terminal.saveHistory(input);
        } else if (terminal.isConnected()) {
            if (StrUtil.isEmpty(input)) {
                HelpTerminalCommandHandler commandHandler = SpringUtil.getBean(HelpTerminalCommandHandler.class);
                TerminalExecuteResult result = commandHandler.execute(null, terminal);
                terminal.appendLine((String) result.getResult());
                terminal.outputPrompt();
            } else {
                TerminalKeyHandler.super.onEnterKeyPressed(terminal);
            }
        }
        return false;
    }
}
