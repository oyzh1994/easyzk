package cn.oyzh.easyzk.zk;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKSASLConfig;
import cn.oyzh.easyzk.store.ZKConnectStore;
import cn.oyzh.easyzk.store.ZKSASLConfigStore;
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
    private static final ZKConnectStore CONNECT_STORE = ZKConnectStore.INSTANCE;

    /**
     * sasl配置存储
     */
    private static final ZKSASLConfigStore CONFIG_STORE = ZKSASLConfigStore.INSTANCE;

    /**
     * 注册配置类
     */
    public static void registerConfiguration() {
        Security.setProperty("login.configuration.provider", ZKSASLConfiguration.class.getName());
    }

    /**
     * 移除sasl配置
     *
     * @param iid zk连接id
     * @see ZKConnect
     */
    public synchronized static void removeSasl(String iid) {
        if (Configuration.getConfiguration() instanceof ZKSASLConfiguration configuration) {
            configuration.removeAppConfigurationEntry(iid);
        }
    }

    /**
     * 是否开启sasl
     *
     * @param iid zk连接id
     * @return 结果
     * @see ZKConnect
     */
    public static boolean isNeedSasl(String iid) {
        if (Configuration.getConfiguration() instanceof ZKSASLConfiguration configuration) {
            // 缓存里存在直接返回
            if (configuration.containsAppConfigurationEntry(iid)) {
                return true;
            }
            SelectParam selectParam = new SelectParam();
            selectParam.addQueryParam(QueryParam.of("id", iid));
            selectParam.addQueryColumn("saslAuth");
            ZKConnect connect = CONNECT_STORE.selectOne(selectParam);
            if (connect != null && connect.isSASLAuth()) {
                ZKSASLConfig config = CONFIG_STORE.getByIid(iid);
                if (config == null || config.checkInvalid()) {
                    return false;
                }
                // 添加到缓存
                if ("Digest".equalsIgnoreCase(config.getType())) {
                    Map<String, String> options = new HashMap<>();
                    options.put("username", config.getUserName());
                    options.put("password", config.getPassword());
                    AppConfigurationEntry entry = new AppConfigurationEntry("org.apache.zookeeper.server.auth.DigestLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
                    configuration.putAppConfigurationEntry(config.getIid(), entry);
                }
                return true;
            }
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
