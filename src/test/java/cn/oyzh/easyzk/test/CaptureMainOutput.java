package cn.oyzh.easyzk.test;

import cn.oyzh.common.thread.DownLatch;
import cn.oyzh.common.thread.ThreadUtil;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeperMain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;


public class CaptureMainOutput {
    public static void main(String[] args) {
        // 保存原始的标准输出流
//        PrintStream originalOut = System.out;

        try {
            // 创建一个 ByteArrayOutputStream 用于捕获输出
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            PrintStream printStream = new PrintStream(outputStream);

            // 将标准输出流重定向到新的 PrintStream
//            System.setOut(printStream);

            // 调用目标类的 main 方法
            String[] mainArgs = {};

            DownLatch latch=DownLatch.of(1);

            ThreadUtil.start(()->{
                try {
                    ZooKeeperMain.main(mainArgs);
                } catch (KeeperException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    latch.countDown();
                }
            });

            // 刷新输出流
//            printStream.flush();

            latch.await();

            // 获取捕获的输出


//            while (true){
//                String output = printStream.toString();
//                // 打印捕获的输出
//                System.out.println("Captured output:");
//                System.out.println(output);
//            }
//            // 恢复原始的标准输出流
//            System.setOut(originalOut);

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
//            // 确保标准输出流被恢复
//            System.setOut(originalOut);
        }
    }
}    