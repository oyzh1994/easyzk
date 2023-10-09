package cn.oyzh.easyzk.shell;

import lombok.experimental.UtilityClass;

/**
 * zk终端工具
 *
 * @author oyzh
 * @since 2023/09/20
 */
@UtilityClass
public class ZKShellUtil {

    // /**
    //  * 格式化输出
    //  *
    //  * @param value 值
    //  * @return 结果
    //  */
    // public static String formatOut(Object value) {
    //     if (value == null) {
    //         return "";
    //     }
    //     if (value instanceof Boolean b) {
    //         return "\"" + (b ? "1" : "0") + "\"";
    //     }
    //     return "\"" + value + "\"";
    // }
    //
    // /**
    //  * 格式化输出
    //  *
    //  * @param values 值
    //  * @return 结果
    //  */
    // public static String formatOut(Collection<?> values) {
    //     if (CollUtil.isEmpty(values)) {
    //         return "";
    //     }
    //     StringBuilder builder = new StringBuilder();
    //     for (Object value : values) {
    //         builder.append(", ").append(value);
    //     }
    //     builder.replace(0, 2, "[");
    //     builder.append("]");
    //     return builder.toString();
    // }

    // /**
    //  * 格式化输出
    //  *
    //  * @param values 值
    //  * @return 结果
    //  */
    // public static String formatOut(Collection<?> values, Stat stat) {
    //     StringBuilder builder = new StringBuilder();
    //     builder.append(formatOut(values));
    //     builder.append("\n");
    //     builder.append(formatOut(stat));
    //     return builder.toString();
    // }

    // /**
    //  * 格式化输出
    //  *
    //  * @param stat 状态
    //  * @return 结果
    //  */
    // public static String formatOut(Stat stat) {
    //     StringBuilder builder = new StringBuilder();
    //     if (stat != null) {
    //         builder.append("cZxid = ").append(stat.getCzxid()).append("\n");
    //         builder.append("ctime = ").append(TIME_FORMAT.format(stat.getCtime())).append("\n");
    //         builder.append("mZxid = ").append(stat.getMzxid()).append("\n");
    //         builder.append("mtime = ").append(TIME_FORMAT.format(stat.getMtime())).append("\n");
    //         builder.append("pZxid = ").append(stat.getPzxid()).append("\n");
    //         builder.append("cversion = ").append(stat.getCversion()).append("\n");
    //         builder.append("dataVersion = ").append(stat.getVersion()).append("\n");
    //         builder.append("aclVersion = ").append(stat.getAversion()).append("\n");
    //         builder.append("ephemeralOwner = ").append(stat.getEphemeralOwner()).append("\n");
    //         builder.append("dataLength = ").append(stat.getDataLength()).append("\n");
    //         builder.append("numChildren = ").append(stat.getNumChildren());
    //     }
    //     return builder.toString();
    // }

    // /**
    //  * 格式化输出
    //  *
    //  * @param data 值
    //  * @param stat 状态
    //  * @return 结果
    //  */
    // public static String formatOut(String data, Stat stat) {
    //     StringBuilder builder = new StringBuilder();
    //     if (data != null) {
    //         builder.append(data).append("\n");
    //     }
    //     builder.append(formatOut(stat));
    //     return builder.toString();
    // }

    /**
     * 获取路径
     *
     * @param input 输入
     * @return 路径
     */
    public static String getPath(String input) {
        String[] strArr = input.split(" ");
        for (String str : strArr) {
            if (str.startsWith("-")) {
                continue;
            }
            if (str.startsWith("/")) {
                return str;
            }
        }
        return null;
    }
}
