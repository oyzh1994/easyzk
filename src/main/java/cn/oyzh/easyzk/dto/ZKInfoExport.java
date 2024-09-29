package cn.oyzh.easyzk.dto;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.common.json.JSONUtil;
import cn.oyzh.fx.common.log.JulLog;
import lombok.Getter;
import lombok.NonNull;
import org.h2.util.json.JSONObject;

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
        JSONObject object = JSONUtil.parseObj(json);
        ZKInfoExport export = new ZKInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getStr("version");
        export.connects = object.getBeanList("connects", ZKInfo.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJsonStr(this);
    }
}
