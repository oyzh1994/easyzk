package cn.oyzh.easyzk.test;

import cn.oyzh.fx.plus.controls.svg.SVGLoader;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2022/5/30
 */
public class SVGTest {

    @Test
    public void test1() {
        SVGLoader loader = new SVGLoader();
        System.out.println(loader.load("plus"));
    }

    @Test
    public void test2() {
        SVGLoader loader = new SVGLoader();
        System.out.println(loader.load("font/style1/list-unordered.svg"));
    }
}
