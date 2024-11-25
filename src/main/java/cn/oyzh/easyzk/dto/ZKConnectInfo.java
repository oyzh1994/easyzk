package cn.oyzh.easyzk.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * zk连接对象
 *
 * @author oyzh
 * @since 2023/9/20
 */
@Getter
public class ZKConnectInfo {

    /**
     * 原始输入内容
     */
    @Setter
    private String input;

    /**
     * 地址
     */
    @Setter
    private String host = "localhost";

    /**
     * 端口
     */
    @Setter
    private int port = 2181;

    /**
     * 超时时间
     */
    @Setter
    private int timeout = 5000;

    /**
     * 只读模式
     */
    @Setter
    private boolean readonly;
}
