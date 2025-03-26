package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.fx.plus.font.FontManager;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextArea;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;
import javafx.scene.text.Font;

/**
 * @author oyzh
 * @since 2024/12/29
 */
public class ZKDataTextAreaPane extends RichDataTextAreaPane {

    @Override
    public void initNode() {
        RichDataTextArea textArea = super.getContent();
        // 200k
        textArea.setStyleBound(RichDataType.HEX, 200 * 1024 * 1024);
        // 500k
        textArea.setStyleBound(RichDataType.JSON, 500 * 1024 * 1024);
        // 100k
        textArea.setStyleBound(RichDataType.BINARY, 100 * 1024 * 1024);
        super.initNode();
    }

    @Override
    protected Font initFont() {
//        // 禁用字体管理
//        super.disableFont();
        // 初始化字体
        ZKSetting setting = ZKSettingStore.SETTING;
//        this.setFontSize(setting.getEditorFontSize());
//        this.setFontFamily(setting.getEditorFontFamily());
//        this.setFontWeight2(setting.getEditorFontWeight());
        return FontManager.toFont(setting.editorFontConfig());
    }
}
