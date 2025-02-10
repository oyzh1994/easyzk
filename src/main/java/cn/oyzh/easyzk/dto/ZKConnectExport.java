package cn.oyzh.easyzk.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONObject;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easyzk.domain.ZKConnect;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * zk连接导出对象
 *
 * @author oyzh
 * @since 2023/2/22
 */
@Getter
public class ZKConnectExport {

    /**
     * 导出程序版本号
     */
    private String version;

    /**
     * 平台
     */
    private String platform;

    /**
     * 导出连接数据
     */
    private List<ZKConnect> connects;

    /**
     * 从zk连接数据生成
     *
     * @param zkConnects 连接列表
     * @return ZKConnectExport
     */
    public static ZKConnectExport fromConnects(@NonNull List<ZKConnect> zkConnects) {
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
    public static ZKConnectExport fromJSON(@NonNull String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        ZKConnectExport export = new ZKConnectExport();
        export.connects = new ArrayList<>(12);
        export.version = object.getString("version");
        export.connects = object.getBeanList("connects", ZKConnect.class);
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
