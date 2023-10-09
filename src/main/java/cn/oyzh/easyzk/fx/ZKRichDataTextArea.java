package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.common.util.TextUtil;
import cn.oyzh.fx.plus.rich.FlexRichTextArea;
import cn.oyzh.fx.plus.rich.RichTextStyle;
import cn.oyzh.fx.plus.util.FXUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * zk消息文本域
 *
 * @author oyzh
 * @since 2023/04/08
 */
public class ZKRichDataTextArea extends FlexRichTextArea {

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
        this.hideLineNum();
        String stringData = TextUtil.getStringData(rawData);
        this.setText(stringData);
        this.initTextStyle();
        this.showType = 0;
    }

    /**
     * 显示json数据
     */
    public void showJsonData(Object rawData) {
        this.showLineNum();
        String jsonData = TextUtil.getJsonData(rawData);
        this.setText(jsonData);
        this.initTextStyle();
        this.showType = 1;
    }

    /**
     * 显示二进制数据
     */
    public void showBinaryData(Object rawData) {
        this.hideLineNum();
        String binaryData = TextUtil.getBinaryData(rawData);
        this.setText(binaryData);
        this.initTextStyle();
        this.showType = 2;
    }

    /**
     * 显示十六进制数据
     */
    public void showHexData(Object rawData) {
        this.hideLineNum();
        String hexData = TextUtil.getHexData(rawData);
        this.setText(hexData);
        this.initTextStyle();
        this.showType = 3;
    }

    /**
     * 显示原始数据
     */
    public void showRawData(Object rawData) {
        this.hideLineNum();
        if (rawData instanceof CharSequence sequence) {
            this.setText(sequence.toString());
        } else if (rawData instanceof byte[] bytes) {
            this.setText(StringUtil.toBinary(bytes));
        }
        this.initTextStyle();
        this.showType = 4;
    }

    /**
     * json符号正则模式
     */
    private static Pattern Json_Symbol_Pattern;

    private static Pattern jsonSymbolPattern() {
        if (Json_Symbol_Pattern == null) {
            Json_Symbol_Pattern = Pattern.compile("[{}|\\[\\]]");
        }
        return Json_Symbol_Pattern;
    }

    /**
     * json键正则模式
     */
    private static Pattern Json_Key_Pattern;

    private static Pattern jsonKeyPattern() {
        if (Json_Key_Pattern == null) {
            Json_Key_Pattern = Pattern.compile("\"(\\w*)\"\\s*:");
        }
        return Json_Key_Pattern;
    }

    /**
     * json值正则模式
     */
    private static Pattern Json_Value_Pattern;

    private static Pattern jsonValuePattern() {
        if (Json_Value_Pattern == null) {
            Json_Value_Pattern = Pattern.compile("(?<=:)\\s*(\"[^\"]*\"|\\d+|true|false|\\[.*?]|\\{.*?})");
        }
        return Json_Value_Pattern;
    }

    @Override
    public void initTextStyle() {
        // json
        if (this.showType == 1) {
            FXUtil.runLater(() -> {
                String text = this.getText();
                Matcher matcher1 = jsonSymbolPattern().matcher(text);
                List<RichTextStyle> styles = new ArrayList<>();
                while (matcher1.find()) {
                    styles.add(new RichTextStyle(matcher1.start(), matcher1.end(), "-fx-fill: #4169E1;"));
                }
                Matcher matcher2 = jsonKeyPattern().matcher(text);
                while (matcher2.find()) {
                    styles.add(new RichTextStyle(matcher2.start() + 1, matcher2.end() - 2, "-fx-fill: #EE2C2C;"));
                }
                Matcher matcher3 = jsonValuePattern().matcher(text);
                while (matcher3.find()) {
                    styles.add(new RichTextStyle(matcher3.start() + 1, matcher3.end() - 1, "-fx-fill: green;"));
                }
                for (RichTextStyle style : styles) {
                    this.setStyle(style);
                }
                this.forgetHistory();
            });
        } else if (this.showType == 2) {// binary
            FXUtil.runLater(() -> {
                this.setStyle(0, this.getLength(), "-fx-fill: #32CD32;");
                this.forgetHistory();
            });
        } else if (this.showType == 3) {// hex
            FXUtil.runLater(() -> {
                this.setStyle(0, this.getLength(), "-fx-fill: #4682B4;");
                this.forgetHistory();
            });
        } else {
            this.clearTextStyle();
        }
    }
}
