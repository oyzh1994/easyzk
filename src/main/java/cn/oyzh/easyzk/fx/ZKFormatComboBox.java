package cn.oyzh.easyzk.fx;


import cn.oyzh.fx.plus.controls.combo.FlexComboBox;

/**
 * zk格式下拉框
 *
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKFormatComboBox extends FlexComboBox<String> {

    {
        this.getItems().add("字符串");
        this.getItems().add("JSON串");
        this.getItems().add("二进制");
        this.getItems().add("十六进制");
        this.getItems().add("原始内容");
    }

    /**
     * 获取格式
     *
     * @return 格式
     */
    public String getFormat() {
        int val = this.getSelectedIndex();
        return switch (val) {
            case 1 -> "JSON";
            case 2 -> "BINARY";
            case 3 -> "HEX";
            case 4 -> "RAW";
            default -> "STRING";
        };
    }

    /**
     * 是否raw格式
     *
     * @return 结果
     */
    public boolean isRawFormat() {
        return "RAW".equals(this.getFormat());
    }

    /**
     * 是否json格式
     *
     * @return 结果
     */
    public boolean isJsonFormat() {
        return "JSON".equals(this.getFormat());
    }

    /**
     * 是否二进制格式
     *
     * @return 结果
     */
    public boolean isBinaryFormat() {
        return "BINARY".equals(this.getFormat());
    }

    /**
     * 是否十六进制格式
     *
     * @return 结果
     */
    public boolean isHexFormat() {
        return "HEX".equals(this.getFormat());
    }

    /**
     * 是否字符串格式
     *
     * @return 结果
     */
    public boolean isStringFormat() {
        return "STRING".equals(this.getFormat());
    }

    /**
     * 选择字符串格式
     */
    public void selectString() {
        this.select(0);
    }

    /**
     * 选择二进制格式
     */
    public void selectBinary() {
        this.select(2);
    }

    /**
     * 选择十六进制格式
     */
    public void selectHex() {
        this.select(3);
    }

    /**
     * 选择json格式
     */
    public void selectJson() {
        this.select(1);
    }
}
