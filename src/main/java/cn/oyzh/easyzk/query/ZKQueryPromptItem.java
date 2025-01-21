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
     * 1 database
     * 2 table
     * 3 column
     * 4 keyword
     * 5 view
     * 6 function
     * 7 procedure
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
     * 是否数据库类型
     *
     * @return 结果
     */
    public boolean isDatabaseType() {
        return 1 == this.type;
    }

    /**
     * 是否表类型
     *
     * @return 结果
     */
    public boolean isTableType() {
        return 2 == this.type;
    }

    /**
     * 是否字段类型
     *
     * @return 结果
     */
    public boolean isColumnType() {
        return 3 == this.type;
    }

    /**
     * 是否关键字类型
     *
     * @return 结果
     */
    public boolean isKeywordType() {
        return 4 == this.type;
    }

    /**
     * 是否视图类型
     *
     * @return 结果
     */
    public boolean isViewType() {
        return 5 == this.type;
    }

    /**
     * 是否函数类型
     *
     * @return 结果
     */
    public boolean isFunctionType() {
        return 6 == this.type;
    }

    /**
     * 是否过程类型
     *
     * @return 结果
     */
    public boolean isProcedureType() {
        return 7 == this.type;
    }

    public String wrapContent( ) {
        return this.content;
    }
}
