package cn.oyzh.easyzk.test;

import cn.oyzh.common.util.TextUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2022/5/30
 */
public class StrTest {

    @Test
    public void test1() {
        System.out.println("1234".substring(1));
    }

    @Test
    public void test2() {
        List<String> list = new ArrayList<>();
        list.add("aa1");
        list.add("bb");
        list.add("cc");
        list.add("dd1");
        list.add("ff1");
        list.add("ee11");
        list.add("33311");
        list.add("测试11");
        list.add("测试1133");
        System.out.println(TextUtil.beautifyFormat(list, 4, 0));
    }
}
