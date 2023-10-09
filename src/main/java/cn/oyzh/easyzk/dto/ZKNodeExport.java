package cn.oyzh.easyzk.dto;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * zk节点导出对象
 *
 * @author oyzh
 * @since 2023/2/20
 */
@Slf4j
public class ZKNodeExport {

    /**
     * 脚本前缀，txt格式专用
     */
    @Getter
    @Setter
    private String prefix;

    /**
     * 导出程序版本号
     */
    @Getter
    @Setter
    private String version;

    /**
     * 平台
     */
    @Getter
    @Setter
    private String platform;

    /**
     * 字符集
     */
    @Getter
    @Setter
    private String charset;

    /**
     * 导出节点数据
     */
    @Getter
    @Setter
    private List<Map<String, String>> nodes;

    /**
     * 转成json字符串
     *
     * @param prettyFormat 美化
     * @return json字符串
     */
    public String toJSONString(boolean prettyFormat) {
        return JSONObject.toJSONString(this, prettyFormat);
    }

    /**
     * 获取数据总数
     *
     * @return 数据总数
     */
    public int counts() {
        return this.nodes == null ? 0 : this.nodes.size();
    }

    /**
     * 获取数据字节数组
     *
     * @param data    数据
     * @param charset 字符集
     * @return 数据字节数组
     */
    public byte[] getDateBytes(String data,@NonNull Charset charset) {
        // if (StrUtil.isBlank(charset)) {
        //     charset = StandardCharsets.UTF_8.name();
        // } else if ("跟随系统".equals(charset)) {
        //     charset = Charset.defaultCharset().name();
        // }
        byte[] bytes;
        try {
            if (data == null) {
                bytes = new byte[]{};
            } else if (data.isEmpty()) {
                bytes = "".getBytes(charset);
            } else {
                bytes = data.getBytes(charset);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            bytes = new byte[]{};
        }
        return bytes;
    }

    /**
     * 是否有前缀
     *
     * @return 结果
     */
    public boolean hasPrefix() {
        return StrUtil.isNotBlank(this.prefix);
    }

    public String version() {
        return this.version == null ? "未知" : this.version;
    }

    public String platform() {
        return this.platform == null ? "未知" : this.platform;
    }

    public String charset() {
        return this.charset == null ? "未知" : this.charset;
    }
}
