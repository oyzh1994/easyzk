package cn.oyzh.easyzk.dto;

import lombok.Getter;
import lombok.Setter;

/**
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

    @Getter
    @Setter
    private String host = "localhost";

    @Getter
    @Setter
    private int port = 2181;

    @Getter
    @Setter
    private int timeout = 5000;

    @Getter
    @Setter
    private boolean readOnly ;
}
