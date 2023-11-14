package cn.oyzh.easyzk.test;

import cn.hutool.core.lang.PatternPool;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2022/5/16
 */
public class RegTest {

    @Test
    public void test1() {
        System.out.println(PatternPool.NUMBERS.matcher("77").matches());
        System.out.println(PatternPool.NUMBERS.matcher("77.").matches());
        System.out.println(PatternPool.NUMBERS.matcher("77.1").matches());
        System.out.println(PatternPool.NUMBERS.matcher(" ").matches());
        System.out.println(PatternPool.MOBILE.matcher("15779011311").matches());
    }
}
