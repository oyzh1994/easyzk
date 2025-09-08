package cn.oyzh.easyzk.terminal;

import cn.oyzh.easyzk.terminal.basic.ZKConnectTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.basic.ZKVersionTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKAddAuthTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKCloseTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKCreateTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKDelQuotaTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKDeleteTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKDeleteallTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKGetAclTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKGetAllChildrenNumberCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKGetConfigTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKGetEphemeralsCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKGetTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKListQuotaTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKLs2TerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKLsTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKReconfigTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKRemoveWatchesTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKRmrTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKSetAclTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKSetQuotaTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKSetTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKStatTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKSyncTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKWhoAmITerminalCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKConfCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKConsCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKCrstCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKDirsCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKDumpCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKEnviCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKKillCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKMntrCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKReqsCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKRuokCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKSrstCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKSrvrCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKStatCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKWchcCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKWchpCommandHandler;
import cn.oyzh.easyzk.terminal.fourletterword.ZKWchsCommandHandler;
import cn.oyzh.fx.terminal.standard.ClearTerminalCommandHandler;
import cn.oyzh.fx.terminal.standard.HelpTerminalCommandHandler;
import cn.oyzh.fx.terminal.util.TerminalManager2;

/**
 * @author oyzh
 * @since 2024-12-30
 */

public class ZKTerminalManager {

    /**
     * 注册处理器
     */
    public static void registerHandlers() {
        // 标准命令
        TerminalManager2.registerHandler(HelpTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ClearTerminalCommandHandler.class);

        // 基础命令
        TerminalManager2.registerHandler(ZKConnectTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKVersionTerminalCommandHandler.class);

        // zk命令
        TerminalManager2.registerHandler(ZKAddAuthTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKCloseTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKCreateTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKDeleteallTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKDeleteTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKDelQuotaTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKGetAclTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKGetAllChildrenNumberCommandHandler.class);
        TerminalManager2.registerHandler(ZKGetConfigTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKGetEphemeralsCommandHandler.class);
        TerminalManager2.registerHandler(ZKGetTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKListQuotaTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKLs2TerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKLsTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKReconfigTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKRemoveWatchesTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKRmrTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKSetAclTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKSetQuotaTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKSetTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKStatTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKSyncTerminalCommandHandler.class);
        TerminalManager2.registerHandler(ZKWhoAmITerminalCommandHandler.class);

        // 四字命令
        TerminalManager2.registerHandler(ZKConfCommandHandler.class);
        TerminalManager2.registerHandler(ZKConsCommandHandler.class);
        TerminalManager2.registerHandler(ZKCrstCommandHandler.class);
        TerminalManager2.registerHandler(ZKDirsCommandHandler.class);
        TerminalManager2.registerHandler(ZKDumpCommandHandler.class);
        TerminalManager2.registerHandler(ZKEnviCommandHandler.class);
        TerminalManager2.registerHandler(ZKKillCommandHandler.class);
        TerminalManager2.registerHandler(ZKMntrCommandHandler.class);
        TerminalManager2.registerHandler(ZKReqsCommandHandler.class);
        TerminalManager2.registerHandler(ZKRuokCommandHandler.class);
        TerminalManager2.registerHandler(ZKSrstCommandHandler.class);
        TerminalManager2.registerHandler(ZKSrvrCommandHandler.class);
        TerminalManager2.registerHandler(ZKStatCommandHandler.class);
        TerminalManager2.registerHandler(ZKWchcCommandHandler.class);
        TerminalManager2.registerHandler(ZKWchpCommandHandler.class);
        TerminalManager2.registerHandler(ZKWchsCommandHandler.class);
    }
}
