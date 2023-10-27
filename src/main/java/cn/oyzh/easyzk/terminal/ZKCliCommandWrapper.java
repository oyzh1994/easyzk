package cn.oyzh.easyzk.terminal;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.cli.ParseException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.cli.CliCommand;

import java.io.IOException;
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

    public ZKCliCommandWrapper(CliCommand command) {
        this.command = command;
    }

    public void init(ZooKeeper zooKeeper) {
        if (!this.initialized) {
            this.initialized = true;
            this.command.setZk(zooKeeper);
            this.command.setOut(new ZKCliPrintStream() {
                @Override
                public void onResponse(String response) {
                    if (onResponse != null) {
                        onResponse.accept(response);
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

    public CliCommand parse(String[] cmdArgs) throws ParseException {
        this.command.parse(cmdArgs);
        return this.command;
    }

    public boolean exec() throws IOException, InterruptedException, KeeperException {
        return this.command.exec();
    }
}
