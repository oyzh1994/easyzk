package cn.oyzh.easyzk.exception;

/**
 * zk异常
 *
 * @author oyzh
 * @since 2020/11/3
 */
public class ZKException extends RuntimeException {

    public ZKException() {
        super();
    }

    public ZKException(String message) {
        super(message);
    }

}
