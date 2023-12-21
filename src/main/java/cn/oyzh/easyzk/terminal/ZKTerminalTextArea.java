package cn.oyzh.easyzk.terminal;

import cn.hutool.log.StaticLog;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKConnect;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.exception.ZKExceptionParser;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.terminal.TerminalTextArea;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.zookeeper.ZooKeeper;

/**
 * zk终端文本域
 *
 * @author oyzh
 * @since 2023/7/21
 */
public class ZKTerminalTextArea extends TerminalTextArea {

    {
        this.keyHandler(ZKTerminalKeyHandler.INSTANCE);
        this.helpHandler(ZKTerminalHelpHandler.INSTANCE);
        this.mouseHandler(ZKTerminalMouseHandler.INSTANCE);
        this.historyHandler(ZKTerminalHistoryHandler.INSTANCE);
        this.completeHandler(ZKTerminalCompleteHandler.INSTANCE);
    }

    /**
     * zk客户端
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKClient client;

    /**
     * zk连接
     */
    private ZKConnect connect;

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
            str = "zk连接";
        } else {
            str = this.client.infoName();
        }
        if (this.info().getHost() != null) {
            str += "@" + this.info().getHost();
        }
        if (this.isConnecting()) {
            str += "（连接中）> ";
        } else if (this.isConnected()) {
            if (this.client.isReadonly()) {
                str += "（已连接/只读模式）> ";
            } else {
                str += "（已连接）> ";
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
    public void init(@NonNull ZKClient client) {
        this.client = client;
        this.disableInput();
        this.outputLine("欢迎使用EasyZK!");
        this.outputLine("Powered By oyzh(2020-2023).");
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
        return this.info().getId() == null;
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
        this.connect = ZKConnectUtil.parse(input);
        if (this.connect != null) {
            this.disable();
            ZKConnectUtil.copyConnect(this.connect, this.info());
            this.start();
        }
    }

    /**
     * 临时连接处理
     */
    private void initByTemporary() {
        this.outputLine("请输入信息然后回车");
        this.outputLine(" connect [-timeout timeout] [-server server] [-r]");
        this.outputLine("-timeout 超时时间，单位毫秒");
        this.outputLine("-server 连接地址，ip:端口");
        this.outputLine("-r 只读模式");
        this.appendByPrompt("connect -timeout 3000 -server localhost:2181");
        this.enableInput();
        this.flushAndMoveCaretAnd();
    }

    /**
     * 常驻连接处理
     */
    private void initByPermanent() {
        this.start();
    }

    /**
     * 开始连接
     */
    private void start() {
        ExecutorUtil.start(() -> {
            try {
                this.intStatListener();
                this.client.start();
            } catch (Exception ex) {
                this.onError(ZKExceptionParser.INSTANCE.apply(ex));
            } finally {
                this.enable();
            }
        }, 10);
    }

    /**
     * 刷新光标并移动到尾部
     */
    private void flushAndMoveCaretAnd() {
        ExecutorUtil.start(() -> {
            this.flushCaret();
            this.moveCaretEnd();
        }, 50);
    }

    /**
     * 初始化连接状态监听器
     */
    private void intStatListener() {
        if (this.stateChangeListener == null) {
            this.stateChangeListener = (observableValue, state, t1) -> {
                this.flushPrompt();
                // 获取连接
                String host = this.info().getHost();
                if (t1 == ZKConnState.CONNECTED) {
                    this.outputLine(host + " 连接成功.");
                    this.outputLine("输入\"help\"或者按下tab键可查看命令列表.");
                    this.outputLine("输入\"命令 -?\"可查看此命令详情.");
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == ZKConnState.CLOSED) {
                    this.outputLine(host + " 连接关闭.");
                    this.enableInput();
                } else if (t1 == ZKConnState.CONNECTING) {
                    this.outputLine(host + " 开始连接.");
                } else if (t1 == ZKConnState.SUSPENDED) {
                    this.outputLine(host + " 连接中断.");
                    this.enableInput();
                } else if (t1 == ZKConnState.RECONNECTED) {
                    this.outputLine(host + " 连接恢复.");
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == ZKConnState.LOST) {
                    this.outputLine(host + " 连接丢失.");
                    this.enableInput();
                } else if (t1 == ZKConnState.FAILED) {
                    this.outputLine(host + " 连接失败.");
                    if (this.connect != null) {
                        this.appendByPrompt(this.connect.getInput());
                    }
                    this.flushAndMoveCaretAnd();
                    this.enableInput();
                }
                StaticLog.info("connState={}", t1);
            };
            this.client().addStateListener(this.stateChangeListener);
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

    public ZKInfo info() {
        return this.client().zkInfo();
    }
}
