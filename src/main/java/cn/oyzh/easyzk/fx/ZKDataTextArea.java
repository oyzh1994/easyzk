//package cn.oyzh.easyzk.fx;
//
//import cn.oyzh.easyzk.domain.ZKSetting;
//import cn.oyzh.easyzk.store.ZKSettingStore;
//import cn.oyzh.fx.plus.font.FontManager;
//import cn.oyzh.fx.rich.RichDataType;
//import cn.oyzh.fx.rich.incubator.RichDataTextArea;
//import javafx.scene.text.Font;
//
///**
// * @author oyzh
// * @since 2024/12/29
// */
//public class ZKDataTextArea extends RichDataTextArea {
//
//    @Override
//    public void initNode() {
//        // 200k
//        this.setStyleBound(RichDataType.HEX, 200 * 1024 * 1024);
//        // 500k
//        this.setStyleBound(RichDataType.JSON, 500 * 1024 * 1024);
//        // 100k
//        this.setStyleBound(RichDataType.BINARY, 100 * 1024 * 1024);
//        super.initNode();
//    }
//
//    @Override
//    protected Font initFont() {
//        // 初始化字体
//        ZKSetting setting = ZKSettingStore.SETTING;
//        return FontManager.toFont(setting.editorFontConfig());
//    }
//}
