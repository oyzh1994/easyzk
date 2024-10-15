package cn.oyzh.easyzk.handler;

import cn.oyzh.easyzk.zk.ZKClient;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author oyzh
 * @since 2024/09/06
 */
@Slf4j
public class ZKDataTransportHandler extends DataHandler {

    /**
     * 来源客户端
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected ZKClient sourceClient;

    /**
     * 目标客户端
     */
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    protected ZKClient targetClient;

    /**
     * 执行传输
     */
    public void doTransport() throws Exception {

    }
}

