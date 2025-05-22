package cn.oyzh.easyzk.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.domain.ZKGroup;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * zk连接导出对象
 *
 * @author oyzh
 * @since 2023/2/22
 */
public class ZKConnectExport {

    /**
     * 导出程序版本号
     */
    private String version;

    /**
     * 平台
     */
    private String platform;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public List<ZKGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ZKGroup> groups) {
        this.groups = groups;
    }

    public List<ZKConnect> getConnects() {
        return connects;
    }

    public void setConnects(List<ZKConnect> connects) {
        this.connects = connects;
    }

    /**
     * 连接
     */
    private List<ZKGroup> groups;

    /**
     * 连接
     */
    private List<ZKConnect> connects;

    /**
     * 从zk连接数据生成
     *
     * @param zkConnects 连接列表
     * @return ZKConnectExport
     */
    public static ZKConnectExport fromConnects( List<ZKConnect> zkConnects) {
        ZKConnectExport export = new ZKConnectExport();
        Project project = Project.load();
        export.version = project.getVersion();
        export.connects = zkConnects;
        export.platform = System.getProperty("os.name");
        return export;
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return ZKInfoExport
     */
    public static ZKConnectExport fromJSON( String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        ZKConnectExport export = new ZKConnectExport();
        export.connects = new ArrayList<>(12);
        export.version = object.getString("version");
        export.platform = object.getString("platform");
        export.groups = object.getList("groups", ZKGroup.class);
        export.connects = object.getList("connects", ZKConnect.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJson(this);
    }
}
