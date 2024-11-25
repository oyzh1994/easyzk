package cn.oyzh.easyzk.store;

import cn.oyzh.common.SysConst;
import cn.oyzh.common.json.JSONArray;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.common.thread.ThreadUtil;
import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.common.util.FileUtil;
import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.domain.ZKCollect;
import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.domain.ZKPageInfo;
import cn.oyzh.easyzk.domain.ZKSSHConnect;
import cn.oyzh.easyzk.domain.ZKSearchHistory;
import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.store.jdbc.JdbcConst;
import cn.oyzh.store.jdbc.JdbcDialect;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024-09-23
 */
@UtilityClass
public class ZKStoreUtil {

    /**
     * 执行初始化
     */
    public static void init() {
        try {
            JdbcConst.dbCacheSize(1024);
            JdbcConst.dbDialect(JdbcDialect.H2);
            JdbcConst.dbFile(ZKConst.STORE_PATH + "db");
        } catch (Exception ex) {
            ex.printStackTrace();
            // 提示
            MessageBox.error(I18nHelper.initStoreFail());
            // 退出程序
            ThreadUtil.start(() -> System.exit(-1), 3000);
        }
    }

    public static void migration() {
        try {
            // 迁移配置数据
            if (FileUtil.exist(ZKSettingStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKSettingStore.INSTANCE.init();
                // 读取配置
                ZKSetting setting = ZKSettingStore.INSTANCE.load();
                // 复制配置
                ZKSettingStore2.SETTING.copy(setting);
                // 执行迁移
                ZKSettingStore2.INSTANCE.replace(setting);
                // 转移旧文件
                FileUtil.move(new File(ZKSettingStore.INSTANCE.filePath()), new File(ZKSettingStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("配置数据迁移成功");
            }

            // 迁移页面数据
            if (FileUtil.exist(ZKPageInfoStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKPageInfoStore.INSTANCE.init();
                // 读取配置
                ZKPageInfo pageInfo = ZKPageInfoStore.INSTANCE.load();
                // 设置配置
                ZKSetting setting = ZKSettingStore2.SETTING;
                setting.setPageWidth(pageInfo.getWidth());
                setting.setPageHeight(pageInfo.getHeight());
                setting.setPageScreenX(pageInfo.getScreenX());
                setting.setPageScreenY(pageInfo.getScreenY());
                setting.setPageMaximized(pageInfo.isMaximized());
                setting.setPageLeftWidth(pageInfo.getMainLeftWidth());
                // 执行迁移
                ZKSettingStore2.INSTANCE.replace(setting);
                // 转移旧文件
                FileUtil.move(new File(ZKPageInfoStore.INSTANCE.filePath()), new File(ZKPageInfoStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("页面数据迁移成功");
            }

            // 迁移分组数据
            if (FileUtil.exist(ZKGroupStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKGroupStore.INSTANCE.init();
                // 读取配置
                List<ZKGroup> groups = ZKGroupStore.INSTANCE.load();
                // 执行迁移
                for (ZKGroup group : groups) {
                    ZKGroupStore2.INSTANCE.replace(group);
                }
                // 转移旧文件
                FileUtil.move(new File(ZKGroupStore.INSTANCE.filePath()), new File(ZKGroupStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("分组数据迁移成功");
            }

            // 迁移认证数据
            if (FileUtil.exist(ZKAuthStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKAuthStore.INSTANCE.init();
                // 读取配置
                List<ZKAuth> list = ZKAuthStore.INSTANCE.load();
                // 执行迁移
                for (ZKAuth auth : list) {
                    ZKAuthStore2.INSTANCE.replace(auth);
                }
                // 转移旧文件
                FileUtil.move(new File(ZKAuthStore.INSTANCE.filePath()), new File(ZKAuthStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("认证数据迁移成功");
            }

            // 迁移过滤数据
            if (FileUtil.exist(ZKFilterStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKFilterStore.INSTANCE.init();
                // 读取配置
                List<ZKFilter> list = ZKFilterStore.INSTANCE.load();
                // 执行迁移
                for (ZKFilter filter : list) {
                    ZKFilterStore2.INSTANCE.replace(filter);
                }
                // 转移旧文件
                FileUtil.move(new File(ZKFilterStore.INSTANCE.filePath()), new File(ZKFilterStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("过滤数据迁移成功");
            }

            // 迁移搜索数据
            if (FileUtil.exist(ZKSearchHistoryStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKSearchHistoryStore.INSTANCE.init();
                // 读取配置
                List<ZKSearchHistory> list = ZKSearchHistoryStore.INSTANCE.load();
                // 执行迁移
                for (ZKSearchHistory history : list) {
                    ZKSearchHistoryStore2.INSTANCE.replace(history);
                }
                // 转移旧文件
                FileUtil.move(new File(ZKSearchHistoryStore.INSTANCE.filePath()), new File(ZKSearchHistoryStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("搜索数据迁移成功");
            }

            // 迁移信息数据
            if (FileUtil.exist(ZKInfoStore.INSTANCE.filePath())) {
                // 手动执行初始化
                ZKInfoStore.INSTANCE.init();
                // 读取配置
                List<ZKInfo> list = ZKInfoStore.INSTANCE.load();
                // 执行迁移
                for (ZKInfo info : list) {
                    boolean result = ZKInfoStore2.INSTANCE.replace(info);
                    if (result) {
                        // 处理收藏
                        if (CollectionUtil.isNotEmpty(info.getCollects())) {
                            for (String collect : info.getCollects()) {
                                ZKCollectStore.INSTANCE.replace(new ZKCollect(info.getId(), collect));
                            }
                        }
                        // 处理ssh
                        ZKSSHConnect sshInfo = info.getSshConnect();
                        if (sshInfo != null) {
                            sshInfo.setIid(info.getId());
                            ZKSSHInfoStore.INSTANCE.replace(sshInfo);
                        }
                    }
                }
                // 转移旧文件
                FileUtil.move(new File(ZKInfoStore.INSTANCE.filePath()), new File(ZKInfoStore.INSTANCE.filePath() + ".bak"), true);
                JulLog.info("信息数据迁移成功");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // 提示
            MessageBox.error(I18nHelper.initConfigFail());
            // 退出程序
            ThreadUtil.start(() -> System.exit(-1), 3000);
        }
    }

    public static List<ZKGroup> loadGroups() {
        List<ZKGroup> groups = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "zk_group.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到分组数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ZKGroup group = new ZKGroup();
                if (obj.containsKey("gid")) {
                    group.setGid(obj.getString("gid"));
                }
                if (obj.containsKey("name")) {
                    group.setName(obj.getString("name"));
                }
                if (obj.containsKey("expand")) {
                    group.setExpand(obj.getBooleanValue("Expand"));
                }
                groups.add(group);
            }
        }
        return groups;
    }

    public static List<ZKInfo> loadConnects() {
        List<ZKInfo> connects = new ArrayList<>();
        String storePath = SysConst.storeDir();
        String file = storePath + File.separator + "zk_info.json";
        String json = FileUtil.readUtf8String(file);
        JSONArray array = JSONUtil.parseArray(json);
        if (array == null) {
            JulLog.warn("未找到分组数据");
        } else {
            for (int i = 0; i < array.size(); i++) {
                JSONObject obj = array.getJSONObject(i);
                ZKInfo connect = new ZKInfo();

                if (obj.containsKey("id")) {
                    connect.setId(obj.getString("id"));
                }
                if (obj.containsKey("name")) {
                    connect.setName(obj.getString("name"));
                }
                if (obj.containsKey("host")) {
                    connect.setHost(obj.getString("host"));
                }
                if (obj.containsKey("listen")) {
                    connect.setListen(obj.getBooleanValue("listen"));
                }
                if (obj.containsKey("sshForward")) {
                    connect.setSshForward(obj.getBooleanValue("sshForward"));
                }
                if (obj.containsKey("collects")) {
                    connect.setCollects(obj.getBeanList("collects", String.class));
                }
                if (obj.containsKey("remark")) {
                    connect.setRemark(obj.getString("remark"));
                }
                if (obj.containsKey("groupId")) {
                    connect.setGroupId(obj.getString("groupId"));
                }
                if (obj.containsKey("readonly")) {
                    connect.setReadonly(obj.getBooleanValue("readonly"));
                }
                if (obj.containsKey("compatibility")) {
                    connect.setCompatibility(obj.getIntValue("compatibility"));
                }
                if (obj.containsKey("sessionTimeOut")) {
                    connect.setSessionTimeOut(obj.getIntValue("sessionTimeOut"));
                }
                if (obj.containsKey("connectTimeOut")) {
                    connect.setConnectTimeOut(obj.getIntValue("connectTimeOut"));
                }
                if (obj.containsKey("sshInfo")) {
                    JSONObject object = obj.getJSONObject("sshInfo");
                    ZKSSHConnect sshConnect = new ZKSSHConnect();
                    if(object.containsKey("port")){
                        sshConnect.setPort(object.getInt("port"));
                    }
                    if(object.containsKey("host")){
                        sshConnect.setHost(object.getString("host"));
                    }
                    if(object.containsKey("user")){
                        sshConnect.setUser(object.getString("user"));
                    }
                    if(object.containsKey("timeout")){
                        sshConnect.setTimeout(object.getInt("timeout"));
                    }
                    if(object.containsKey("password")){
                        sshConnect.setPassword(object.getString("password"));
                    }
                    connect.setSshConnect(sshConnect);
                }
                connects.add(connect);
            }
        }
        return connects;
    }
}
