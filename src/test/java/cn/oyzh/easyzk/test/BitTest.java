package cn.oyzh.easyzk.test;

import org.junit.Test;

import java.util.BitSet;

public class BitTest {


    @Test
    public void test1() {
        BitSet bitSet = new BitSet(0b00000001);
        bitSet.set(0, false);
        bitSet.set(1, true);
        System.out.println(bitSet.get(0));
        System.out.println(bitSet.get(1));
    }
}
