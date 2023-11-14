package cn.oyzh.easyzk.test;

import cn.hutool.core.util.CharsetUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author oyzh
 * @since 2023/9/14
 */
public class CharsetTest {


    @Test
    public void test1() throws UnsupportedEncodingException {

        System.out.println(Charset.defaultCharset());
        byte[] bytes = new byte[]{-17, -65, -67, -17, -65, -67, -17, -65, -67, -17, -65, -67, 50};
        System.out.println(new String(bytes));
        System.out.println(new String(bytes, "gbk"));
        System.out.println(new String(bytes, "gb2312"));
        System.out.println(new String(bytes, "gb18030"));
        System.out.println(new String(bytes, "utf8"));
        System.out.println(new String(bytes, "utf16"));
        System.out.println(new String(bytes, StandardCharsets.UTF_16BE));
        System.out.println(new String(bytes, StandardCharsets.UTF_16LE));
        System.out.println(new String(bytes, StandardCharsets.US_ASCII));
        System.out.println(new String(bytes, StandardCharsets.ISO_8859_1));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.defaultCharset(), StandardCharsets.ISO_8859_1));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.defaultCharset(), StandardCharsets.UTF_8));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.defaultCharset(), StandardCharsets.US_ASCII));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.defaultCharset(), StandardCharsets.UTF_16));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.defaultCharset(), StandardCharsets.UTF_16BE));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.defaultCharset(), StandardCharsets.UTF_16LE));

        System.out.println(CharsetUtil.convert(new String(bytes), Charset.forName("gbk"), StandardCharsets.UTF_8));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.forName("gb18030"), StandardCharsets.UTF_8));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.forName("utf8"), CharsetUtil.CHARSET_GBK));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.forName("utf8"), CharsetUtil.charset("gb2312")));
        System.out.println(CharsetUtil.convert(new String(bytes), Charset.forName("utf8"), CharsetUtil.charset("gb18030")));
    }
}
