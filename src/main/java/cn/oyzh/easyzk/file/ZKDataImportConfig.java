package cn.oyzh.easyzk.file;

import cn.oyzh.common.util.StringUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.charset.StandardCharsets;

/**
 * @author oyzh
 * @since 2024/09/02
 */
@Data
@Accessors(chain = true, fluent = true)
public class ZKDataImportConfig {

    /**
     * 日期格式
     */
    private String dateFormat;

    /**
     * 导入模式
     * 1. 追加
     * 2. 复制
     */
    private String importMode = "2";

    /**
     * 字段索引
     */
    private int columnIndex = 0;

    /**
     * 数据起始索引
     */
    private int dataStartIndex = 1;

    /**
     * 字段标签
     */
    private String recordLabel;

    /**
     * 属性作为字段
     */
    private boolean attrToColumn;

    /**
     * 记录分割符号
     */
    private String recordSeparator = System.lineSeparator();

    /**
     * 字段分割符号
     */
    private String fieldSeparator = ";";

    /**
     * 文本识别符号
     */
    private String txtIdentifier = "\"";

    /**
     * 字符集
     */
    private String charset = StandardCharsets.UTF_8.displayName();

    public boolean isAppendMode() {
        return StringUtil.equals(this.importMode, "1");
    }

    public boolean isCopyMode() {
        return StringUtil.equals(this.importMode, "2");
    }

    public char fieldSeparatorChar() {
        return this.fieldSeparator.charAt(0);
    }

    public char txtIdentifierChar() {
        return this.txtIdentifier.charAt(0);
    }
}
