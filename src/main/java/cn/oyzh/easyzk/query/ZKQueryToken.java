package cn.oyzh.easyzk.query;

import cn.oyzh.common.util.StringUtil;
import lombok.Data;
import lombok.ToString;

/**
 * @author oyzh
 * @since 2024/8/15
 */
@Data
@ToString
public class ZKQueryToken {

    /**
     * 结束位置
     */
    private int endIndex;

    /**
     * 开始位置
     */
    private int startIndex;

    /**
     * 内容
     */
    private String content;

    /**
     * 1 空格
     * 2 .
     * 3 `
     */
    private Character token;

    public boolean isEmpty() {
        return StringUtil.isEmpty(this.content);
    }

    public boolean isNotEmpty() {
        return StringUtil.isNotEmpty(this.content);
    }

    public boolean isPossibilityKeyword() {
        return this.token == null || ' ' == this.token;
    }
}
