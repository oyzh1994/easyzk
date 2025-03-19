package cn.oyzh.easyzk.action;


/**
 * @author oyzh
 * @since 2025-01-02
 */
@Data
public class ZKClientActionArgument {

    private Object value;

    private String argument;

    public ZKClientActionArgument(String argument, Object value) {
        this.argument = argument;
        this.value = value;
    }

    public ZKClientActionArgument(String argument) {
        this.argument = argument;
    }

    public ZKClientActionArgument(Object value) {
        this.value = value;
    }

    public static ZKClientActionArgument ofArgument(Object value) {
        return new ZKClientActionArgument(value);
    }

    public static ZKClientActionArgument ofArgument(String argument) {
        return new ZKClientActionArgument(argument);
    }

    public static ZKClientActionArgument ofArgument(String arg, Object value) {
        return new ZKClientActionArgument(arg, value);
    }

}
