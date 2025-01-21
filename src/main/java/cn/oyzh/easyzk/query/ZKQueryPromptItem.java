package cn.oyzh.easyzk.query;

import lombok.Data;

/**
 * 查询提示内容
 *
 * @author oyzh
 * @since 2024/02/21
 */
@Data
public class ZKQueryPromptItem {

    /**
     * 类型
     * 1 关键字
     * 2 节点
     * 3 参数
     */
    private byte type;

    /**
     * 内容
     */
    private String content;

    /**
     * 相关度
     */
    private double correlation;

    /**
     * 额外内容
     */
    private String extContent;

    /**
     * 是否关键字类型
     *
     * @return 结果
     */
    public boolean isKeywordType() {
        return 1 == this.type;
    }

    /**
     * 是否节点类型
     *
     * @return 结果
     */
    public boolean isNodeType() {
        return 2 == this.type;
    }

    /**
     * 是否参数类型
     *
     * @return 结果
     */
    public boolean isParamType() {
        return 3 == this.type;
    }
}
