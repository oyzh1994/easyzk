package cn.oyzh.easyzk.dto;

import cn.oyzh.fx.common.dto.Project;
import cn.oyzh.fx.common.util.SpringUtil;
import cn.oyzh.easyzk.domain.ZKInfo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * zk连接导出对象
 *
 * @author oyzh
 * @since 2023/2/22
 */
@Slf4j
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
        Project project = SpringUtil.getBean(Project.class);
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
        log.info("json: {}", json);
        JSONObject object = JSONObject.parseObject(json);
        ZKInfoExport export = new ZKInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getString("version");
        JSONArray nodes = object.getJSONArray("connects");
        export.connects = nodes.toJavaList(ZKInfo.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }
}
