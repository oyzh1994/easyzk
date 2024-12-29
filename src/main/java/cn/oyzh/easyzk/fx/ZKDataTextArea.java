package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.store.ZKSettingStore;
import cn.oyzh.fx.rich.richtextfx.data.RichDataTextAreaPane;

public class ZKDataTextArea extends RichDataTextAreaPane {

    @Override
    protected void initFont() {
        // 禁用字体管理
        super.disableFont();
        // 初始化字体
        ZKSetting setting= ZKSettingStore.SETTING;
        this.setFontSize(setting.getEditorFontSize());
        this.setFontFamily(setting.getEditorFontFamily());
        this.setFontWeight(setting.getEditorFontWeight());
    }
}
