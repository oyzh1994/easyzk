package cn.oyzh.easyzk.fx;


import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;

import java.util.List;
import java.util.Locale;

/**
 * zk格式下拉框
 *
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKDataFormatComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
        this.setTipText(I18nHelper.format());
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

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        if (locale == Locale.TRADITIONAL_CHINESE) {
            this.addItem("字符串");
            this.addItem("JSON串");
            this.addItem("二進制");
            this.addItem("十六進制");
            this.addItem("原始内容");
        } else if (locale == Locale.SIMPLIFIED_CHINESE) {
            this.addItem("字符串");
            this.addItem("JSON串");
            this.addItem("二进制");
            this.addItem("十六进制");
            this.addItem("原始内容");
        } else {
            this.addItem("STRING");
            this.addItem("JSON");
            this.addItem("BINARY");
            this.addItem("HEX");
            this.addItem("RAW");
        }
        return this.getItems();
    }
}
