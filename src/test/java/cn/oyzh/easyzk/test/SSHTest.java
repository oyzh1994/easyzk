//package cn.oyzh.easyzk.test;
//
//import cn.oyzh.easyzk.domain.ZKConnect;
//import cn.oyzh.easyzk.domain.ZKSSHConfig;
//import cn.oyzh.easyzk.zk.ZKClient;
//import cn.oyzh.ssh.jump.SSHJumpForwarder;
//import org.junit.Test;
//
///**
// * @author oyzh
// * @since 2023/12/14
// */
//public class SSHTest {
//
//    @Test
//    public void test() throws Exception {
//        ZKSSHConfig connectInfo = new ZKSSHConfig();
//        connectInfo.setHost("192.168.189.130");
//        connectInfo.setUser("root");
//        connectInfo.setPassword("123456");
////
////        SSHJumpConfig forwardInfo = new SSHJumpConfig();
////        forwardInfo.setRemotePort(2181);
////        forwardInfo.setRemoteHost("192.168.189.134");
//
//        SSHJumpForwarder forwarder = new SSHJumpForwarder();
//        int localPort = forwarder.forward(null, null);
//
//        ZKConnect info = new ZKConnect();
//        info.setHost("127.0.0.1:" + localPort);
//        info.setConnectTimeOut(3000);
//        ZKClient client = new ZKClient(info);
//        client.start();
//        System.out.println(client.setData("/", "val1".getBytes()));
//        System.out.println(new String(client.getData("/")));
//
//    }
//}
