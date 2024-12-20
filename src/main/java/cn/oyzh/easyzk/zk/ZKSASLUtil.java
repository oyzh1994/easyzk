package cn.oyzh.easyzk.zk;

import cn.oyzh.common.file.FileUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
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

    private static final ZKSASLConfigJdbcStore configStore = ZKSASLConfigJdbcStore.INSTANCE;

    private  static boolean needUpdate;

    public synchronized static void updateJaasFile() {
        needUpdate = true;
    }

    public static String getJaasFile() {
        String file = ZKConst.STORE_PATH + "jaas.conf";
        if (!FileUtil.exist(file) || needUpdate) {
            // 更新标志位
            needUpdate = false;
            List<ZKSASLConfig> configs = configStore.selectList();
            StringBuilder sb = new StringBuilder();
            for (ZKSASLConfig config : configs) {
                if (config.getIid() == null || config.checkInvalid()) {
                    continue;
                }
                if (StringUtil.equalsIgnoreCase("Digest", config.getType())) {
                    sb.append("Client_").append(config.getIid().replaceAll("-", ""));
                    sb.append(" {\n");
                    sb.append("    ").append("org.apache.zookeeper.server.auth.DigestLoginModule required\n");
                    sb.append("    ").append("username=\"").append(config.getUserName()).append("\"\n");
                    sb.append("    ").append("password=\"").append(config.getPassword()).append("\";\n");
                    sb.append("};\n");
                }
            }
            // 删除旧文件
            FileUtil.del(file);
            // 写入内容
            FileUtil.writeUtf8String(sb.toString(), file);
            // 刷新配置
            Configuration.getConfiguration().refresh();
        }
        if (!FileUtil.exist(file)) {
            return null;
        }
        return file;
    }
}
