package cn.oyzh.easyzk.zk;

import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.store.ZKConnectJdbcStore;
import cn.oyzh.easyzk.store.ZKSASLConfigJdbcStore;
import lombok.experimental.UtilityClass;

import javax.security.auth.login.Configuration;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-12-20
 */
@UtilityClass
public class ZKSASLUtil {

    private static final ZKConnectJdbcStore connectStore = ZKConnectJdbcStore.INSTANCE;

    private static final ZKSASLConfigJdbcStore configStore = ZKSASLConfigJdbcStore.INSTANCE;

    private static boolean needUpdate;

    /**
     * 更新sasl文件
     */
    public synchronized static void updateSaslFile() {
        needUpdate = true;
    }

    /**
     * 是否开启sasl
     *
     * @param iid zk连接id
     * @return 结果
     * @see ZKConnect
     */
    public static boolean isEnableSasl(String iid) {
        ZKConnect connect = connectStore.selectOne(iid);
        if (connect != null && connect.isSASLAuth()) {
            ZKSASLConfig config = configStore.getByIid(iid);
            return config != null && !config.checkInvalid();
        }
        return false;
    }

    /**
     * 获取sasl配置文件
     *
     * @return sasl文件
     */
    public static String getSaslFile() {
        String file = ZKConst.STORE_PATH + "jaas.conf";
        if (!FileUtil.exist(file) || needUpdate) {
            // 更新标志位
            needUpdate = false;
            List<ZKSASLConfig> configs = configStore.selectList();
            StringBuilder sb = new StringBuilder();
            for (ZKSASLConfig config : configs) {
                if (!config.checkInvalid()) {
                    if ("Digest".equalsIgnoreCase(config.getType())) {
                        sb.append("Client_").append(config.getIid().replaceAll("-", ""));
                        sb.append(" {\n");
                        sb.append("    ").append("org.apache.zookeeper.server.auth.DigestLoginModule required\n");
                        sb.append("    ").append("username=\"").append(config.getUserName()).append("\"\n");
                        sb.append("    ").append("password=\"").append(config.getPassword()).append("\";\n");
                        sb.append("};\n");
                    }
                }
            }
            // 更新sasl文件
            if (!sb.isEmpty()) {
                // 写入内容
                FileUtil.writeUtf8String(sb.toString(), file);
                // 刷新配置
                Configuration.getConfiguration().refresh();
            } else {// 删除文件
                FileUtil.del(file);
            }
        }
        // 判断文件是否存在
        if (!FileUtil.exist(file)) {
            return null;
        }
        return file;
    }
}
