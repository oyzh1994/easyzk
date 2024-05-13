package cn.oyzh.easyzk.util;

import cn.oyzh.fx.plus.i18n.I18nResourceBundle;

/**
 * @author oyzh
 * @since 2024/5/13
 */
public class ZKI18nHelper {

    public static final String NODE_TIP1 = "zk.nodeTip1";

    public static final String NODE_TIP2 = "zk.nodeTip2";

    public static final String NODE_TIP3 = "zk.nodeTip3";

    public static final String NODE_TIP4 = "zk.nodeTip4";

    public static String nodeTip1() {
        return I18nResourceBundle.i18nString(NODE_TIP1);
    }

    public static String nodeTip2() {
        return I18nResourceBundle.i18nString(NODE_TIP2);
    }

    public static String nodeTip3() {
        return I18nResourceBundle.i18nString(NODE_TIP3);
    }

    public static String nodeTip4() {
        return I18nResourceBundle.i18nString(NODE_TIP4);
    }
}
