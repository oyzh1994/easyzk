package cn.oyzh.easyzk.terminal;

import cn.oyzh.fx.terminal.histroy.BaseTerminalHistoryHandler;
import cn.oyzh.fx.terminal.histroy.TerminalHistory;

import java.util.ArrayList;
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

    /**
     * 缓存记录
     */
    private final List<ZKTerminalHistory> cecheList = new ArrayList<>();

    /**
     * 存储器
     */
    private final ZKTerminalHistoryJdbcStore historyStore = ZKTerminalHistoryJdbcStore.INSTANCE;

    @Override
    public void clearHistory() {
        this.historyStore.clear();
        this.cecheList.clear();
    }

    @Override
    public List<ZKTerminalHistory> listHistory() {
        if (this.cecheList.isEmpty()) {
            this.cecheList.addAll(this.historyStore.selectList());
        }
        return this.cecheList;
    }

    @Override
    public void addHistory(TerminalHistory history) {
        ZKTerminalHistory terminalHistory = new ZKTerminalHistory();
        terminalHistory.setSaveTime(System.currentTimeMillis());
        terminalHistory.setLine(history.getLine());
        this.historyStore.insert(terminalHistory);
        this.cecheList.add(terminalHistory);
    }
}
