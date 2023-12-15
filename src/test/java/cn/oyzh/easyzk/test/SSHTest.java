package cn.oyzh.easyzk.test;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.ssh.SSHConnectInfo;
import cn.oyzh.fx.common.ssh.SSHForwardInfo;
import cn.oyzh.fx.common.ssh.SSHForwarder;
import org.junit.Test;

/**
 * @author oyzh
 * @since 2023/12/14
 */
public class SSHTest {

    @Test
    public void test() throws Exception {
        SSHConnectInfo connectInfo = new SSHConnectInfo();
        connectInfo.setHost("192.168.189.130");
        connectInfo.setUser("root");
        connectInfo.setPassword("123456");

        SSHForwardInfo forwardInfo = new SSHForwardInfo();
        forwardInfo.setPort(2181);
        forwardInfo.setHost("192.168.189.134");

        SSHForwarder forwarder = new SSHForwarder(connectInfo);
        int localPort = forwarder.forward(forwardInfo);

        ZKInfo info = new ZKInfo();
        info.setHost("127.0.0.1:" + localPort);
        info.setConnectTimeOut(3000);
        ZKClient client = new ZKClient(info);
        client.start();
        System.out.println(client.setData("/", "val1".getBytes()));
        System.out.println(new String(client.getData("/")));

    }
}
