package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.FlexComboBox;

/**
 * zk格式下拉框
 *
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKFormatComboBox extends FlexComboBox<String> {

    {
        this.getItems().add("STRING(字符串)");
        this.getItems().add("JSON(JSON串)");
        this.getItems().add("BINARY(二进制)");
        this.getItems().add("HEX(十六进制)");
        this.getItems().add("RAW(原始)");
    }

    /**
     * 获取格式
     *
     * @return 格式
     */
    public String getFormat() {
        String val = this.getValue();
        if (val == null || val.isEmpty()) {
            return null;
        }
        return val.substring(0, val.indexOf("("));
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
     * 选择二进制格式
     */
    public void selectBinary() {
        for (String item : this.getItems()) {
            if (item.contains("BINARY")) {
                this.select(item);
                break;
            }
        }
    }

    /**
     * 选择十六进制格式
     */
    public void selectHex() {
        for (String item : this.getItems()) {
            if (item.contains("HEX")) {
                this.select(item);
                break;
            }
        }
    }

    /**
     * 选择json格式
     */
    public void selectJson() {
        for (String item : this.getItems()) {
            if (item.contains("JSON")) {
                this.select(item);
                break;
            }
        }
    }
}
