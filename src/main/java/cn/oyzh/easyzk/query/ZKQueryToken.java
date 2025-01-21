package cn.oyzh.easyzk.query;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.util.ZKNodeUtil;
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
     * 1 null
     * 2 空格
     */
    private Character token;

    public boolean isEmpty() {
        return StringUtil.isEmpty(this.content);
    }

    public boolean isNotEmpty() {
        return StringUtil.isNotEmpty(this.content);
    }

    public boolean isPossibilityKeyword() {
        return this.token == null;
    }

    public boolean isPossibilityNode() {
        return this.token != null && this.isNotEmpty() && this.token == ' ' && this.getPath() != null;
    }

    public boolean isPossibilityParam() {
        return this.token != null && this.token == '-';
    }

    public String getPath() {
        String[] arr = this.content.split(" ");
        for (String s : arr) {
            if (s.startsWith("/")) {
                return ZKNodeUtil.getParentPath(s);
            }
        }
        return null;
    }
}
