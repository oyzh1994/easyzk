package cn.oyzh.easyzk.file;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * @author oyzh
 * @since 2024/09/02
 */
@Data
@Accessors(chain = true, fluent = true)
public class ZKDataExportConfig {

    /**
     * 文本识别符号
     */
    private String txtIdentifier = "\"";

    /**
     * 字符集
     */
    private String charset = StandardCharsets.UTF_8.name();

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 前缀
     */
    private String prefix;

}
