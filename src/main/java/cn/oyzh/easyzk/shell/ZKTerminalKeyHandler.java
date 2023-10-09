package cn.oyzh.easyzk.shell;

import cn.oyzh.fx.terminal.Terminal;
import cn.oyzh.fx.terminal.key.BaseTerminalKeyHandler;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class ZKTerminalKeyHandler extends BaseTerminalKeyHandler {

    /**
     * 当前实例
     */
    public static final ZKTerminalKeyHandler INSTANCE = new ZKTerminalKeyHandler();

    @Override
    public boolean onEnterKeyPressed(Terminal terminal) throws Exception {
        ZKTerminalTextArea textArea = (ZKTerminalTextArea) terminal;
        String input = terminal.getInput();
        if (textArea.isTemporary() && !textArea.isConnected()) {
            textArea.connect(input);
            terminal.saveHistory(input);
        } else if (textArea.isConnected()) {
            super.onEnterKeyPressed(terminal);
        }
        return false;
    }
}
