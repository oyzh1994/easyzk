package cn.oyzh.easyzk.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * zk连接对象
 *
 * @author oyzh
 * @since 2023/9/20
 */
public class ZKConnect {

    /**
     * 原始输入内容
     */
    @Getter
    @Setter
    private String input;

    /**
     * 地址
     */
    @Getter
    @Setter
    private String host = "localhost";

    /**
     * 端口
     */
    @Getter
    @Setter
    private int port = 2181;

    /**
     * 超时时间
     */
    @Getter
    @Setter
    private int timeout = 5000;

    /**
     * 只读模式
     */
    @Getter
    @Setter
    private boolean readOnly;
}
