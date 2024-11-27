package cn.oyzh.easyzk.terminal;

import cn.oyzh.fx.terminal.histroy.BaseTerminalHistoryHandler;
import cn.oyzh.fx.terminal.histroy.TerminalHistory;

import java.util.List;

/**
 * @author oyzh
 * @since 2023/8/28
 */
public class ZKTerminalHistoryHandler extends BaseTerminalHistoryHandler {

    /**
     * 当前实例
     */
    public static final ZKTerminalHistoryHandler INSTANCE = new ZKTerminalHistoryHandler();

    private final ZKTerminalHistoryJdbcStore historyStore = new ZKTerminalHistoryJdbcStore();

    @Override
    public void clearHistory() {
        this.historyStore.clear();
    }

    @Override
    public List<ZKTerminalHistory> listHistory() {
        return this.historyStore.selectList();
    }

    @Override
    public void addHistory(TerminalHistory history) {
        ZKTerminalHistory terminalHistory = new ZKTerminalHistory();
        terminalHistory.setSaveTime(System.currentTimeMillis());
        terminalHistory.setLine(history.getLine());
        this.historyStore.insert(terminalHistory);
    }
}
