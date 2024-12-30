package cn.oyzh.easyzk.terminal;

import cn.oyzh.easyzk.terminal.basic.ZKConnectTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.basic.ZKVersionTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKAddAuthTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKCloseTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKCreateTerminalCommandHandler;
import cn.oyzh.easyzk.terminal.cli.ZKDelQuotaTerminalCommandHandler;
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
import cn.oyzh.fx.terminal.util.TerminalManager;
import lombok.experimental.UtilityClass;

/**
 * @author oyzh
 * @since 2024-12-30
 */
@UtilityClass
public class ZKTerminalManager {

    /**
     * 注册处理器
     */
    public static void registerHandlers() {
        // 标准命令
        TerminalManager.registerHandler(HelpTerminalCommandHandler.class);
        TerminalManager.registerHandler(ClearTerminalCommandHandler.class);

        // 基础命令
        TerminalManager.registerHandler(ZKConnectTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKVersionTerminalCommandHandler.class);

        // zk命令
        TerminalManager.registerHandler(ZKAddAuthTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKCloseTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKCreateTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKDeleteallTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKDelQuotaTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKGetAclTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKGetAllChildrenNumberCommandHandler.class);
        TerminalManager.registerHandler(ZKGetConfigTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKGetEphemeralsCommandHandler.class);
        TerminalManager.registerHandler(ZKGetTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKListQuotaTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKLs2TerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKLsTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKReconfigTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKRemoveWatchesTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKRmrTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKSetAclTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKSetQuotaTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKSetTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKStatTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKSyncTerminalCommandHandler.class);
        TerminalManager.registerHandler(ZKWhoAmITerminalCommandHandler.class);

        // 四字命令
        TerminalManager.registerHandler(ZKConfCommandHandler.class);
        TerminalManager.registerHandler(ZKConsCommandHandler.class);
        TerminalManager.registerHandler(ZKCrstCommandHandler.class);
        TerminalManager.registerHandler(ZKDirsCommandHandler.class);
        TerminalManager.registerHandler(ZKDumpCommandHandler.class);
        TerminalManager.registerHandler(ZKEnviCommandHandler.class);
        TerminalManager.registerHandler(ZKKillCommandHandler.class);
        TerminalManager.registerHandler(ZKMntrCommandHandler.class);
        TerminalManager.registerHandler(ZKReqsCommandHandler.class);
        TerminalManager.registerHandler(ZKRuokCommandHandler.class);
        TerminalManager.registerHandler(ZKSrstCommandHandler.class);
        TerminalManager.registerHandler(ZKSrvrCommandHandler.class);
        TerminalManager.registerHandler(ZKStatCommandHandler.class);
        TerminalManager.registerHandler(ZKWchcCommandHandler.class);
        TerminalManager.registerHandler(ZKWchpCommandHandler.class);
        TerminalManager.registerHandler(ZKWchsCommandHandler.class);
    }
}
