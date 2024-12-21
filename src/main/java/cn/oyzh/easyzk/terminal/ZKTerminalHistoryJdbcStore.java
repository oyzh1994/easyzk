package cn.oyzh.easyzk.terminal;

import cn.oyzh.store.jdbc.JdbcStandardStore;

/**
 * @author oyzh
 * @since 2024-11-25
 */
public class ZKTerminalHistoryJdbcStore extends JdbcStandardStore<ZKTerminalHistory> {

    /**
     * 当前实例
     */
    public static final ZKTerminalHistoryJdbcStore INSTANCE = new ZKTerminalHistoryJdbcStore();

    public boolean replace(ZKTerminalHistory model) {
        return this.insert(model);
    }

    @Override
    protected ZKTerminalHistory newModel() {
        return new ZKTerminalHistory();
    }

    @Override
    protected Class<ZKTerminalHistory> modelClass() {
        return ZKTerminalHistory.class;
    }
}
