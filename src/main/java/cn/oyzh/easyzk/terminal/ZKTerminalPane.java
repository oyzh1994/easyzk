package cn.oyzh.easyzk.terminal;

import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ExecutorUtil;
import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.dto.ZKConnectInfo;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.terminal.TerminalPane;
import cn.oyzh.i18n.I18nHelper;
import javafx.beans.value.ChangeListener;
import javafx.scene.text.Font;
import org.apache.zookeeper.ZooKeeper;

/**
 * zk终端文本域
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class ZKTerminalPane extends TerminalPane {

    {
        this.keyHandler(ZKTerminalKeyHandler.INSTANCE);
        this.helpHandler(ZKTerminalHelpHandler.INSTANCE);
        this.mouseHandler(ZKTerminalMouseHandler.INSTANCE);
        this.historyHandler(ZKTerminalHistoryHandler.INSTANCE);
        this.completeHandler(ZKTerminalCompleteHandler.INSTANCE);
    }

//     @Override
//     public void initNode() {
//         super.initNode();
//         super.initPrompts();
//     }
//
//     @Override
//     protected Font initFont() {
// //        // 禁用字体管理
// //        super.disableFont();
//         // 初始化字体
//         ZKSetting setting = ZKSettingStore.SETTING;
// //        this.setFontSize(setting.getTerminalFontSize());
// //        this.setFontFamily(setting.getTerminalFontFamily());
// //        this.setFontWeight2(setting.getTerminalFontWeight());
//         return FontManager.toFont(setting.terminalFontConfig());
//     }

    /**
     * 编辑器字体
     */
    private Font editorFont;

    @Override
    protected Font getEditorFont() {
        if (this.editorFont == null) {
            ZKSetting setting = ZKSettingStore.SETTING;
            this.editorFont = FontManager.toFont(setting.terminalFontConfig());
        }
        return this.editorFont;
    }

    @Override
    protected void setEditorFont(Font editorFont) {
        this.editorFont = editorFont;
        super.setEditorFont(editorFont);
    }

    @Override
    public void changeFont(Font font) {
        //ZKSetting setting = ZKSettingStore.SETTING;
        //Font font1 = FontManager.toFont(setting.terminalFontConfig());
        //super.changeFont(font1);
        //super.applyEditorFont();
        super.changeFont(font);
    }

    /**
     * zk客户端
     */
    private ZKClient client;

    public ZKClient getClient() {
        return client;
    }

    /**
     * zk连接
     */
    private ZKConnectInfo connectInfo;

    /**
     * 客户端连接状态监听器
     */
    private ChangeListener<ZKConnState> stateChangeListener;

    /**
     * 获取zookeeper对象
     *
     * @return ZooKeeper
     * @throws Exception 异常
     */
    public ZooKeeper zooKeeper() throws Exception {
        return this.client.getZooKeeper();
    }

    @Override
    public void flushPrompt() {
        String str;
        if (this.isTemporary()) {
            str = "zk" + I18nHelper.connect();
        } else {
            str = this.client.connectName();
        }
        if (this.zkConnect().getHost() != null) {
            str += "@" + this.zkConnect().getHost();
        }
        if (this.isConnecting()) {
            str += "(" + I18nHelper.connectIng() + ")> ";
        } else if (this.isConnected()) {
            if (this.client.isReadonly()) {
                str += "(" + I18nHelper.connected() + "/" + I18nHelper.readonlyMode() + ")> ";
            } else {
                str += "(" + I18nHelper.connected() + ")> ";
            }
        } else {
            str += "> ";
        }
        this.prompt(str);
    }

    /**
     * 初始化
     *
     * @param client 客户端
     */
    public void init(ZKClient client) {
        this.client = client;
        this.disableInput();
        this.outputLine(I18nResourceBundle.i18nString("zk.home.welcome"));
        this.outputLine("Powered By oyzh(2020-2025).");
        this.flushPrompt();
        if (this.isTemporary()) {
            this.initByTemporary();
        } else {
            this.initByPermanent();
        }
    }

    /**
     * 是否临时连接
     *
     * @return 结果
     */
    public boolean isTemporary() {
        return this.client.iid() == null;
    }

    @Override
    public void outputPrompt() {
        if (!this.client.isConnecting()) {
            super.outputPrompt();
        }
    }

    /**
     * 是否已连接
     *
     * @return 结果
     */
    public boolean isConnected() {
        return this.client != null && this.client.isConnected();
    }

    /**
     * 是否连接中
     *
     * @return 结果
     */
    public boolean isConnecting() {
        return this.client != null && this.client.isConnecting();
    }

    /**
     * 是否已关闭
     *
     * @return 结果
     */
    public boolean isClosed() {
        return this.client != null && this.client.isClosed();
    }

    /**
     * 执行连接
     *
     * @param input 输入内容
     */
    public void connect(String input) {
        this.connectInfo = ZKConnectUtil.parse(input);
        if (this.connectInfo != null) {
            this.disable();
            ZKConnectUtil.copyConnect(this.connectInfo, this.zkConnect());
            this.start();
        }
    }

    /**
     * 临时连接处理
     */
    private void initByTemporary() {
        this.outputLine("connect [-timeout timeout] [-server server] [-r]");
        this.outputLine("-timeout " + I18nResourceBundle.i18nString("base.unit", "base.ms"));
        this.outputLine("-server ip:" + I18nHelper.port());
        this.outputLine("-r " + I18nHelper.readonlyMode());
        this.appendByPrompt("connect -timeout 3000 -server localhost:2181");
        this.enableInput();
        this.flushAndMoveCaretEnd();
    }

    /**
     * 常驻连接处理
     */
    private void initByPermanent() {
//        this.start();
        this.flushPrompt();
        this.appendByPrompt("");
        this.enableInput();
        this.flushAndMoveCaretEnd();
    }

    /**
     * 开始连接
     */
    private void start() {
        TaskManager.start(() -> {
            try {
                this.initStatListener();
                this.client.start();
            } catch (Exception ex) {
                this.onError(ZKExceptionParser.INSTANCE.apply(ex));
            } finally {
                this.enable();
            }
        });
    }

    /**
     * 刷新光标并移动到尾部
     */
    private void flushAndMoveCaretEnd() {
        ExecutorUtil.start(() -> {
            this.flushCaret();
            this.moveCaretEnd();
        }, 50);
    }

    /**
     * 初始化连接状态监听器
     */
    private void initStatListener() {
        if (this.stateChangeListener == null) {
            this.stateChangeListener = (observableValue, state, t1) -> {
                this.flushPrompt();
                // 获取连接
                String host = this.zkConnect().getHost();
                if (t1 == ZKConnState.CONNECTED) {
                    this.outputLine(host + I18nHelper.connectSuccess() + " .");
                    this.outputLine(I18nHelper.terminalTip2());
                    this.outputLine(I18nHelper.terminalTip1());
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == ZKConnState.CLOSED) {
                    this.outputLine(host + " " + I18nHelper.connectionClosed() + " .");
                    this.enableInput();
                } else if (t1 == ZKConnState.CONNECTING) {
                    this.outputLine(host + " " + I18nHelper.connectionConnecting() + " .", false);
                } else if (t1 == ZKConnState.SUSPENDED) {
                    this.outputLine(host + " " + I18nHelper.connectSuspended() + " .");
                    this.enableInput();
                } else if (t1 == ZKConnState.RECONNECTED) {
                    this.outputLine(host + " " + I18nHelper.connectReconnected() + " .");
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == ZKConnState.LOST) {
                    this.outputLine(host + " " + I18nHelper.connectionLoss() + " .");
                    this.enableInput();
                } else if (t1 == ZKConnState.FAILED) {
                    this.outputLine(host + I18nHelper.connectFail() + " .");
                    if (this.connectInfo != null) {
                        this.appendByPrompt(this.connectInfo.getInput());
                    }
                    this.flushAndMoveCaretEnd();
                    this.enableInput();
                }
                JulLog.info("connState={}", t1);
            };
            this.getClient().addStateListener(this.stateChangeListener);
        }
    }

    @Override
    public void enableInput() {
        if (this.isConnecting()) {
            return;
        }
        if (this.isConnected() || (!this.isConnected() && this.isTemporary())) {
            super.enableInput();
        }
    }

    public ZKConnect zkConnect() {
        return this.getClient().zkConnect();
    }

    @Override
    public void fontSizeIncr() {
        super.fontSizeIncr();
        this.saveFontSize();
    }

    @Override
    public void fontSizeDecr() {
        super.fontSizeDecr();
        this.saveFontSize();
    }

    /**
     * 保存字体大小
     */
    private void saveFontSize() {
        ZKSetting setting = ZKSettingStore.SETTING;
        setting.setTerminalFontSize((byte) this.getFontSize());
        ZKSettingStore.INSTANCE.replace(setting);
    }
}
