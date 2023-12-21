package cn.oyzh.easyzk.terminal;

import lombok.Getter;
import lombok.Setter;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.cli.CliCommand;
import org.apache.zookeeper.cli.CliException;
import org.apache.zookeeper.cli.CliParseException;

import java.util.function.Consumer;

/**
 * Cli命令包装器
 *
 * @author oyzh
 * @since 2023/9/20
 */
public class ZKCliCommandWrapper {

    @Getter
    private final CliCommand command;

    private boolean initialized;

    @Setter
    private Consumer<String> onResponse;

    public ZKCliCommandWrapper(CliCommand command, ZooKeeper zooKeeper) {
        this.command = command;
        this.init(zooKeeper);
    }

    /**
     * 初始化
     *
     * @param zooKeeper zk客户端
     */
    private void init(ZooKeeper zooKeeper) {
        if (!this.initialized) {
            this.initialized = true;
            this.command.setZk(zooKeeper);
            this.command.setOut(new ZKCliPrintStream() {
                @Override
                public void onResponse(String str) {
                    if (onResponse != null) {
                        onResponse.accept(str);
                    }
                }
            });
            this.command.setErr(new ZKCliPrintStream() {
                @Override
                public void onResponse(String response) {
                    if (onResponse != null) {
                        onResponse.accept(response);
                    }
                }
            });
        }
    }

    /**
     * 解析命令
     *
     * @param cmdArgs 命令及参数
     * @return 命令
     * @throws CliParseException 异常
     */
    public CliCommand parse(String[] cmdArgs) throws CliParseException {
        this.command.parse(cmdArgs);
        return this.command;
    }

    /**
     * 执行命令
     *
     * @return 结果
     * @throws CliParseException 异常
     */
    public boolean exec() throws CliException {
        return this.command.exec();
    }
}
