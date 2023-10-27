package cn.oyzh.easyzk.terminal;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.fx.terminal.histroy.TerminalHistoryStore;
import lombok.extern.slf4j.Slf4j;

/**
 * zk终端命令历史
 *
 * @author oyzh
 * @since 2023/7/21
 */
@Slf4j
public class ZKTerminalHistoryStore extends TerminalHistoryStore {

    /**
     * 当前实例
     */
    public static final ZKTerminalHistoryStore INSTANCE = new ZKTerminalHistoryStore();

    {
        this.filePath(ZKConst.STORE_PATH + "zk_shell_history.json");
        log.info("ZKShellHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

}
