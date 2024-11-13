package cn.oyzh.easyzk.test;

import cn.oyzh.common.thread.ThreadUtil;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2023/9/25
 */
public class ThreadTest {

    @Test
    public void test1() {
        for (int i = 0; i < 10000; i++) {
            int finalI = i;
            ThreadUtil.startVirtual(() -> {
                System.out.println("startVirtual=" + finalI);
            });
        }
    }
}
