package cn.oyzh.easyzk.zk;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.store.ZKConnectJdbcStore;
import cn.oyzh.easyzk.store.ZKSASLConfigJdbcStore;
import cn.oyzh.store.jdbc.QueryParam;
import cn.oyzh.store.jdbc.SelectParam;
import lombok.experimental.UtilityClass;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.security.Security;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author oyzh
 * @since 2024-12-20
 */
@UtilityClass
public class ZKSASLUtil {

    /**
     * 连接存储
     */
    private static final ZKConnectJdbcStore CONNECT_STORE = ZKConnectJdbcStore.INSTANCE;

    /**
     * sasl配置存储
     */
    private static final ZKSASLConfigJdbcStore CONFIG_STORE = ZKSASLConfigJdbcStore.INSTANCE;

    /**
     * 更新标志位
     */
    private static boolean needUpdate = true;

    /**
     * 注册配置类
     */
    public static void registerConfiguration() {
        Security.setProperty("login.configuration.provider", ZKSASLConfiguration.class.getName());
    }

    /**
     * 更新sasl文件
     */
    public synchronized static void updateSasl() {
        needUpdate = true;
    }

    /**
     * 是否开启sasl
     *
     * @param iid zk连接id
     * @return 结果
     * @see ZKConnect
     */
    public static boolean isNeedSasl(String iid) {
        if (needUpdate) {
            // 更新标志位
            needUpdate = false;
            // 更新配置
            updateSaslEntry();
        }
        SelectParam selectParam = new SelectParam();
        selectParam.addQueryParam(QueryParam.of("id", iid));
        selectParam.addQueryColumn("saslAuth");
        ZKConnect connect = CONNECT_STORE.selectOne(selectParam);
        if (connect != null && connect.isSASLAuth()) {
            ZKSASLConfig config = CONFIG_STORE.getByIid(iid);
            return config != null && !config.checkInvalid();
        }
        return false;
    }

    /**
     * 更新sasl配置
     */
    private static void updateSaslEntry() {
        List<ZKSASLConfig> configs = CONFIG_STORE.selectList();
        for (ZKSASLConfig config : configs) {
            if (!config.checkInvalid()) {
                if ("Digest".equalsIgnoreCase(config.getType())) {
                    Map<String, String> options = new HashMap<>();
                    options.put("username", config.getUserName());
                    options.put("password", config.getPassword());
                    AppConfigurationEntry entry = new AppConfigurationEntry("org.apache.zookeeper.server.auth.DigestLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
                    if (Configuration.getConfiguration() instanceof ZKSASLConfiguration configuration) {
                        configuration.putAppConfigurationEntry(config.getIid(), entry);
                    }
                }
            }
        }
    }
}
