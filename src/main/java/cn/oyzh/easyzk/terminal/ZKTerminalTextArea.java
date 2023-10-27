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
        this.helpHandler(ZKTerminalHelpHandler.INSTANCE);
        this.historyHandler(ZKTerminalHistoryHandler.INSTANCE);
        this.completeHandler(ZKTerminalCompleteHandler.INSTANCE);
        this.keyHandler(ZKTerminalKeyHandler.INSTANCE);
        this.mouseHandler(ZKTerminalMouseHandler.INSTANCE);
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
     * 获取zookeeper对象
     *
     * @return ZooKeeper
     * @throws Exception 异常
     */
    public ZooKeeper zooKeeper() throws Exception {
        return this.client.getZooKeeper();
    }

    /**
     * redis客户端连接状态监听器
     */
    private ChangeListener<ZKConnState> connStateChangeListener;

    @Override
    public void flushPrompt() {
        if (!this.client.isConnected()) {
            this.prompt("zk连接@" + this.client.infoName() + "> ");
        } else {
            this.prompt("zk连接@" + this.client.infoName() + "（已连接）> ");
        }
    }

    /**
     * 初始化
     *
     * @param client redis客户端
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
        return this.client.zkInfo().getId() == null;
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
            this.client.zkInfo().setHost(connect.getHost() + ":" + connect.getPort());
            this.client.zkInfo().setConnectTimeOut(connect.getTimeout());
            this.intConnStat();
            this.disable();
            ThreadUtil.startVirtual(() -> {
                this.client.start();
                this.enable();
            });
        }
    }

    /**
     * 临时连接处理
     */
    private void initByTemporary() {
        this.outputLine("请输入连接地址然后回车，格式connect [-timeout timeout] -server [host ip:port] [-r]");
        this.appendByPrompt("connect -timeout 3000 -server localhost:2181");
        this.enableInput();
        this.moveAndFlushCaret();
    }

    private void moveAndFlushCaret() {
        ExecutorUtil.start(() -> {
            this.flushCaret();
            this.moveCaretEnd();
        }, 50);
    }

    /**
     * 常驻连接处理
     */
    private void initByPermanent() {
        this.outputLine(this.client.zkInfo().getHost() + " 连接开始.");
        ExecutorUtil.start(() -> {
            this.intConnStat();
            this.client.start();
        }, 10);
    }

    /**
     * 初始化连接状态处理
     */
    private void intConnStat() {
        if (this.connStateChangeListener == null) {
            this.connStateChangeListener = (observableValue, state, t1) -> {
                this.flushPrompt();
                // 获取连接
                String host = this.info().getHost();
                if (t1 == ZKConnState.CONNECTED) {
                    this.outputLine(host + " 连接成功.");
                    this.outputLine("输入\"help\"可查看支持的命令列表.");
                    this.outputLine("输入\"命令 -?\"尝试获取此命令详情.");
                    this.outputPrompt();
                    this.flushCaret();
                    super.enableInput();
                } else if (t1 == ZKConnState.CLOSED) {
                    this.disableInput();
                    this.outputLine(host + " 连接关闭.");
                } else if (t1 == ZKConnState.LOST) {
                    this.disableInput();
                    this.outputLine(host + " 连接中断.");
                } else if (t1 == ZKConnState.FAILED) {
                    this.outputLine(host + " 连接失败.");
                    if (this.connect != null) {
                        this.appendByPrompt(this.connect.getInput());
                    }
                    this.moveAndFlushCaret();
                    this.enableInput();
                }
                log.info("connState={}", t1);
            };
        }
        this.client().addStateListener(this.connStateChangeListener);
    }

    @Override
    public void enableInput() {
        if (this.isConnected() || this.isTemporary()) {
            super.enableInput();
        }
    }

    public ZKInfo info() {
        return this.client().zkInfo();
    }
}
