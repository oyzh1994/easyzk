// package cn.oyzh.easyzk.fx;
//
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.common.util.StringUtil;
// import cn.oyzh.common.util.TextUtil;
// import cn.oyzh.fx.plus.i18n.I18nHelper;
// import cn.oyzh.fx.plus.theme.ThemeManager;
// import cn.oyzh.fx.plus.util.FXUtil;
// import cn.oyzh.fx.rich.RichTextStyle;
// import cn.oyzh.fx.rich.control.FlexRichTextArea;
// import javafx.beans.value.ChangeListener;
// import lombok.Getter;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
//
// /**
//  * zk消息文本域
//  *
//  * @author oyzh
//  * @since 2023/04/08
//  */
// public class ZKRichDataTextArea extends FlexRichTextArea {
//
//     /**
//      * 显示类型
//      * 0 string
//      * 1 json
//      * 2 binary
//      * 3 hex
//      * 4 raw
//      */
//     @Getter
//     private byte showType = 0;
//
//     /**
//      * 是否忽略变化
//      */
//     @Getter
//     private boolean ignoreChange;
//
//     @Override
//     public void addTextChangeListener(ChangeListener<String> listener) {
//         this.textProperty().addListener((observable, oldValue, newValue) -> {
//             if (!this.ignoreChange) {
//                 listener.changed(observable, oldValue, newValue);
//             }
//         });
//     }
//
//     /**
//      * 显示数据
//      *
//      * @param showType 显示类型
//      * @param rawData  显示数据
//      */
//     public void showData(byte showType, Object rawData) {
//         String promptText = this.getPromptText();
//         try {
//             this.ignoreChange = true;
//             this.disable();
//             this.setPromptText(I18nHelper.dataLoading() + "...");
//             this.showType = showType;
//             this.showData(rawData);
//         } finally {
//             this.ignoreChange = false;
//             this.setPromptText(promptText);
//             this.enable();
//         }
//     }
//
//     /**
//      * 显示数据
//      *
//      * @param rawData 显示数据
//      */
//     public void showData(Object rawData) {
//         switch (this.showType) {
//             case 0 -> this.showStringData(rawData);
//             case 1 -> this.showJsonData(rawData);
//             case 2 -> this.showBinaryData(rawData);
//             case 3 -> this.showHexData(rawData);
//             case 4 -> this.showRawData(rawData);
//         }
//     }
//
//     @Override
//     public void setText(String text) {
//         super.setText(text);
//         if (this.isEmpty()) {
//             this.hideLineNum();
//         } else {
//             this.showLineNum();
//         }
//     }
//
//     /**
//      * 显示字符串数据
//      */
//     public void showStringData(Object rawData) {
//         String stringData = TextUtil.getStringData(rawData);
//         this.setText(stringData);
//         this.initTextStyle();
//         this.showType = 0;
//     }
//
//     /**
//      * 显示json数据
//      */
//     public void showJsonData(Object rawData) {
//         String jsonData = TextUtil.getJsonData(rawData);
//         this.setText(jsonData);
//         this.initTextStyle();
//         this.showType = 1;
//     }
//
//     /**
//      * 显示二进制数据
//      */
//     public void showBinaryData(Object rawData) {
//         String binaryData = TextUtil.getBinaryData(rawData);
//         this.setText(binaryData);
//         this.initTextStyle();
//         this.showType = 2;
//     }
//
//     /**
//      * 显示十六进制数据
//      */
//     public void showHexData(Object rawData) {
//         String hexData = TextUtil.getHexData(rawData);
//         this.setText(hexData);
//         this.initTextStyle();
//         this.showType = 3;
//     }
//
//     /**
//      * 显示原始数据
//      */
//     public void showRawData(Object rawData) {
//         if (rawData instanceof CharSequence sequence) {
//             this.setText(sequence.toString());
//         } else if (rawData instanceof byte[] bytes) {
//             this.setText(StringUtil.toBinary(bytes));
//         }
//         this.initTextStyle();
//         this.showType = 4;
//     }
//
//     /**
//      * 搜索正则模式
//      *
//      * @return 搜索正则模式
//      */
//     private Pattern searchPattern() {
//         return Pattern.compile(this.searchText);
//     }
//
//     /**
//      * json符号正则模式
//      */
//     private static Pattern Json_Symbol_Pattern;
//
//     private static Pattern jsonSymbolPattern() {
//         if (Json_Symbol_Pattern == null) {
//             Json_Symbol_Pattern = Pattern.compile("[{}|\\[\\]]");
//         }
//         return Json_Symbol_Pattern;
//     }
//
//     /**
//      * json键正则模式
//      */
//     private static Pattern Json_Key_Pattern;
//
//     private static Pattern jsonKeyPattern() {
//         if (Json_Key_Pattern == null) {
//             Json_Key_Pattern = Pattern.compile("\"([a-zA-Z0-9-_.]+[\\w\\s]*[\\w]+)\":");
//         }
//         return Json_Key_Pattern;
//     }
//
//     /**
//      * json值正则模式
//      */
//     private static Pattern Json_Value_Pattern;
//
//     private static Pattern jsonValuePattern() {
//         if (Json_Value_Pattern == null) {
//             Json_Value_Pattern = Pattern.compile("(?<=:)\\s*(\"[^\"]*\"|\\d+|true|false|\\[.*?]|\\{.*?})");
//         }
//         return Json_Value_Pattern;
//     }
//
//     @Override
//     public void initTextStyle() {
//         FXUtil.runWait(() -> {
//             this.clearTextStyle();
//             // 搜索
//             if (StringUtil.isNotBlank(this.searchText)) {
//                 String text = this.getText();
//                 Matcher matcher = this.searchPattern().matcher(text);
//                 List<RichTextStyle> styles = new ArrayList<>();
//                 while (matcher.find()) {
//                     styles.add(new RichTextStyle(matcher.start(), matcher.end(), "-fx-fill: #FF6600;"));
//                 }
//                 for (RichTextStyle style : styles) {
//                     this.setStyle(style);
//                 }
//             } else if (this.showType == 1) { // json
//                 String text = this.getText();
//                 Matcher matcher1 = jsonSymbolPattern().matcher(text);
//                 List<RichTextStyle> styles = new ArrayList<>();
//                 while (matcher1.find()) {
//                     styles.add(new RichTextStyle(matcher1.start(), matcher1.end(), "-fx-fill: #4169E1;"));
//                 }
//                 Matcher matcher2 = jsonKeyPattern().matcher(text);
//                 while (matcher2.find()) {
//                     styles.add(new RichTextStyle(matcher2.start(), matcher2.end() - 1, "-fx-fill: #EE2C2C;"));
//                 }
//                 Matcher matcher3 = jsonValuePattern().matcher(text);
//                 while (matcher3.find()) {
//                     styles.add(new RichTextStyle(matcher3.start(), matcher3.end(), "-fx-fill: green;"));
//                 }
//                 for (RichTextStyle style : styles) {
//                     this.setStyle(style);
//                 }
//             } else if (this.showType == 2) {// binary
//                 this.setStyle(0, this.getLength(), "-fx-fill: #32CD32;");
//             } else if (this.showType == 3) {// hex
//                 this.setStyle(0, this.getLength(), "-fx-fill: #4682B4;");
//             } else {
//                 super.changeTheme(ThemeManager.currentTheme());
//             }
//         });
//     }
//
//     /**
//      * 搜索文本
//      */
//     @Getter
//     private String searchText;
//
//     /**
//      * 设置搜索文本
//      *
//      * @param searchText 搜索文本
//      */
//     public void setSearchText(String searchText) {
//         this.searchText = searchText;
//         this.initTextStyle();
//     }
// }
