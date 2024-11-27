// package cn.oyzh.easyzk.terminal;
//
// import cn.oyzh.common.log.JulLog;
// import cn.oyzh.easyzk.ZKConst;
// import cn.oyzh.fx.terminal.histroy.TerminalHistoryStore;
//
// /**
//  * zk终端命令历史
//  *
//  * @author oyzh
//  * @since 2023/7/21
//  */
// @Deprecated
// public class ZKTerminalHistoryStore extends TerminalHistoryStore {
//
//     /**
//      * 当前实例
//      */
//     public static final ZKTerminalHistoryStore INSTANCE = new ZKTerminalHistoryStore();
//
//     {
//         this.filePath(ZKConst.STORE_PATH + "zk_shell_history.json");
//         JulLog.info("ZKShellHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//     }
//
// }
