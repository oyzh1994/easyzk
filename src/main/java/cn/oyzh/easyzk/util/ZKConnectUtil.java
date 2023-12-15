package cn.oyzh.easyzk.util;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.dto.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.thread.ThreadUtil;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageWrapper;
import lombok.experimental.UtilityClass;

/**
 * zk连接工具类
 *
 * @author oyzh
 * @since 2022/8/26
 */
//@Slf4j
@UtilityClass
public class ZKConnectUtil {

    /**
     * 测试连接
     *
     * @param view 页面
     * @param info zk信息
     */
    public static void testConnect(StageWrapper view, ZKInfo info) {
        ThreadUtil.startVirtual(() -> {
            try {
                view.disable();
                view.waitCursor();
                view.appendTitle("==连接测试中...");
                ZKClient client = new ZKClient(info);
                // 开始连接
                client.start();
                view.enable();
                view.defaultCursor();
                view.restoreTitle();
                if (client.isConnected()) {
                    client.close();
                    MessageBox.okToast("连接成功！");
                } else {
                    MessageBox.warn("连接失败，请检查地址是否有效！");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox.exception(ex);
            } finally {
                view.enable();
                view.defaultCursor();
                view.restoreTitle();
            }
        });
    }

    // /**
    //  * 测试连接
    //  *
    //  * @param view    页面
    //  * @param host    地址
    //  * @param timeout 超时事件
    //  */
    // public static void testConnect(StageWrapper view, String host, int timeout) {
    //     try {
    //         ThreadUtil.startVirtual(() -> {
    //             view.disable();
    //             view.waitCursor();
    //             view.appendTitle("==连接测试中...");
    //             // 创建zk信息
    //             ZKInfo zkInfo = new ZKInfo();
    //             zkInfo.setHost(host);
    //             zkInfo.setConnectTimeOut(timeout);
    //             ZKClient client = new ZKClient(zkInfo);
    //             // 开始连接
    //             client.start();
    //             view.enable();
    //             view.defaultCursor();
    //             view.restoreTitle();
    //             if (client.isConnected()) {
    //                 client.close();
    //                 MessageBox.okToast("连接成功！");
    //             } else {
    //                 MessageBox.warn("连接失败，请检查地址是否有效！");
    //             }
    //         });
    //     } catch (Exception ex) {
    //         MessageBox.exception(ex, "连接失败！");
    //     }
    // }

    /**
     * 关闭zk客户端
     *
     * @param client zk客户端
     * @param async  是否异步
     */
    public static void close(ZKClient client, boolean async) {
        try {
            if (client != null && client.isConnected()) {
                if (async) {
                    ThreadUtil.startVirtual(client::close);
                } else {
                    client.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 解析连接
     *
     * @param input 输入内容
     * @return 连接
     */
    public static ZKConnect parse(String input) {
        if (input != null) {
            try {
                String[] words = input.split(" ");
                ZKConnect connect = new ZKConnect();
                connect.setInput(input);
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
                    int type;
                    if (word.equalsIgnoreCase("-server")) {
                        type = 0;
                    } else if (word.equalsIgnoreCase("-timeout")) {
                        type = 1;
                    } else if (word.equalsIgnoreCase("-r")) {
                        type = 2;
                    } else {
                        type = -1;
                    }
                    if (type == 0) {
                        String[] strings = words[i + 1].trim().split(":");
                        if (strings.length > 0) {
                            connect.setHost(strings[0]);
                        }
                        if (strings.length > 1) {
                            connect.setPort(Integer.parseInt(strings[1]));
                        }
                    } else if (type == 1) {
                        connect.setTimeout(Integer.parseInt(words[i + 1]) / 1000);
                    } else if (type == 2) {
                        connect.setReadonly(true);
                    }
                }
                return connect;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 复制连接
     *
     * @param connect 连接对象
     * @param info    zk对象
     */
    public static void copyConnect(ZKConnect connect, ZKInfo info) {
        if (connect != null && info != null) {
            info.setReadonly(connect.isReadonly());
            info.setConnectTimeOut(connect.getTimeout());
            info.setHost(connect.getHost() + ":" + connect.getPort());
        }
    }
}
