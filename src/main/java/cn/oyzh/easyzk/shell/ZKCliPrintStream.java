package cn.oyzh.easyzk.shell;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author oyzh
 * @since 2023/9/20
 */
public abstract class ZKCliPrintStream extends PrintStream {

    public ZKCliPrintStream() {
        super(OutputStream.nullOutputStream(), true);
    }

    @Override
    public void println(String x) {
        if (x != null) {
            this.onResponse(x);
        }
    }

    public abstract void onResponse(String response);
}
