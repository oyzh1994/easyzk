package cn.oyzh.easyzk.terminal;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKConnect;
import cn.oyzh.easyzk.enums.ZKConnState;
import cn.oyzh.easyzk.util.ZKConnectUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.ExecutorUtil;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.terminal.TerminalTextArea;
import javafx.beans.value.ChangeListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;

/**
 * zk终端文本域
 *
 * @author oyzh
 * @since 2023/7/21
 */
@Slf4j
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
    private ChangeListener<ZKConnState> connStateChangeListener;

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
            str += "（已连接）> ";
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
            ExecutorUtil.start(() -> {
                this.intStatListener();
                this.client.start();
                this.enable();
            }, 10);
        }
    }

    /**
     * 临时连接处理
     */
    private void initByTemporary() {
        this.outputLine("请输入连接地址然后回车，格式connect [-timeout timeout] -server [host ip:port]");
        this.appendByPrompt("connect -timeout 3000 -server localhost:2181");
        this.enableInput();
        this.flushAndMoveCaretAnd();
    }

    /**
     * 常驻连接处理
     */
    private void initByPermanent() {
//        this.outputLine(this.info().getHost() + " 连接开始.");
        ExecutorUtil.start(() -> {
            this.intStatListener();
            this.client.start();
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
        if (this.connStateChangeListener == null) {
            this.connStateChangeListener = (observableValue, state, t1) -> {
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
                    this.disableInput();
                } else if (t1 == ZKConnState.LOST) {
                    this.outputLine(host + " 连接中断.");
                    this.enableInput();
                } else if (t1 == ZKConnState.FAILED) {
                    this.outputLine(host + " 连接失败.");
                    if (this.connect != null) {
                        this.appendByPrompt(this.connect.getInput());
                    }
                    this.flushAndMoveCaretAnd();
                    this.enableInput();
                }
                log.info("connState={}", t1);
            };
            this.client().addStateListener(this.connStateChangeListener);
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
