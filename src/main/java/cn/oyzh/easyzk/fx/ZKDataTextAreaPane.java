package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextArea;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;
import cn.oyzh.fx.rich.richtextfx.data.RichDataType;

/**
 * @author oyzh
 * @since 2024/12/29
 */
public class ZKDataTextAreaPane extends RichDataTextAreaPane {

    @Override
    protected void initTextArea() {
        RichDataTextArea textArea = super.getContent();
        // 200k
        textArea.setStyleBound(RichDataType.HEX, 200 * 1024 * 1024);
        // 500k
        textArea.setStyleBound(RichDataType.JSON, 500 * 1024 * 1024);
        // 100k
        textArea.setStyleBound(RichDataType.BINARY, 100 * 1024 * 1024);
        super.initTextArea();
    }

    @Override
    protected void initFont() {
        // 禁用字体管理
        super.disableFont();
        // 初始化字体
        ZKSetting setting = ZKSettingStore.SETTING;
        this.setFontSize(setting.getEditorFontSize());
        this.setFontFamily(setting.getEditorFontFamily());
        this.setFontWeight2(setting.getEditorFontWeight());
    }
}
