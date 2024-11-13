package cn.oyzh.easyzk.fx;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.common.util.TextUtil;
import cn.oyzh.fx.plus.controls.area.FlexTextArea;
import lombok.Getter;
import lombok.Setter;

/**
 * zk消息文本域
 *
 * @author oyzh
 * @since 2023/04/08
 */
public class ZKDataTextArea extends FlexTextArea {

    // /**
    //  * 取消自动换行阈值
    //  */
    // public static Integer Cancel_Wrap_Threshold = 100 * 1000;
    //
    // {
    //     this.addTextChangeListener((observable, oldValue, newValue) -> {
    //         if (newValue != null) {
    //             if (newValue.length() > Cancel_Wrap_Threshold) {
    //                 if (this.isWrapText()) {
    //                     this.setWrapText(false);
    //                 }
    //             } else if (!this.isWrapText()) {
    //                 this.setWrapText(true);
    //             }
    //         }
    //     });
    // }

    /**
     * 显示类型
     * 0 string
     * 1 json
     * 2 binary
     * 3 hex
     * 4 raw
     */
    @Setter
    @Getter
    private byte showType = 0;

    /**
     * 显示数据
     */
    public void showData(Object rawData) {
        switch (this.showType) {
            case 0 -> this.showStringData(rawData);
            case 1 -> this.showJsonData(rawData);
            case 2 -> this.showBinaryData(rawData);
            case 3 -> this.showHexData(rawData);
            case 4 -> this.showRawData(rawData);
        }
    }

    /**
     * 显示字符串数据
     */
    public void showStringData(Object rawData) {
        String stringData = TextUtil.getStringData(rawData);
        this.setText(stringData);
        this.showType = 0;
    }

    /**
     * 显示json数据
     */
    public void showJsonData(Object rawData) {
        String jsonData = TextUtil.getJsonData(rawData);
        this.setText(jsonData);
        this.showType = 1;
    }

    /**
     * 显示二进制数据
     */
    public void showBinaryData(Object rawData) {
        String binaryData = TextUtil.getBinaryData(rawData);
        this.setText(binaryData);
        this.showType = 2;
    }

    /**
     * 显示十六进制数据
     */
    public void showHexData(Object rawData) {
        String hexData = TextUtil.getHexData(rawData);
        this.setText(hexData);
        this.showType = 3;
    }

    /**
     * 显示原始数据
     */
    public void showRawData(Object rawData) {
        if (rawData instanceof CharSequence sequence) {
            this.setText(sequence.toString());
        } else if (rawData instanceof byte[] bytes) {
            this.setText(StringUtil.toBinary(bytes));
        }
        this.showType = 4;
    }
}
