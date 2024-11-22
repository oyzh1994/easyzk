package cn.oyzh.easyzk.dto;

import cn.oyzh.common.json.JSONObject;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import com.google.gson.JsonObject;
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
public class ZKInfoExport {

    /**
     * 导出程序版本号
     */
    @Getter
    private String version;

    /**
     * 平台
     */
    @Getter
    private String platform;

    /**
     * 导出连接数据
     */
    @Getter
    private List<ZKInfo> connects;

    /**
     * 从zk连接数据生成
     *
     * @param zkInfos 连接列表
     * @return ZKInfoExport
     */
    public static ZKInfoExport fromConnects(@NonNull List<ZKInfo> zkInfos) {
        ZKInfoExport export = new ZKInfoExport();
        Project project = Project.load();
        export.version = project.getVersion();
        export.connects = zkInfos;
        export.platform = System.getProperty("os.name");
        return export;
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return ZKInfoExport
     */
    public static ZKInfoExport fromJSON(@NonNull String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        ZKInfoExport export = new ZKInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getString("version");
        export.connects = object.getBeanList("connects", ZKInfo.class);
//        export.connects = JSONUtil.toBeanList(object.getJSONArray("connects"), ZKInfo.class);
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
