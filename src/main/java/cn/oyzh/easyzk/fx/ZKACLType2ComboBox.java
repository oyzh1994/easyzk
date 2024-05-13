package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/26
 */
public class ZKACLType2ComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        if (locale == Locale.SIMPLIFIED_CHINESE) {
            this.addItem("开放认证(WORLD)");
            this.addItem("摘要认证(DIGEST)");
            this.addItem("IP认证(IP)");
        } else if (locale == Locale.TRADITIONAL_CHINESE) {
            this.addItem("開放認證(WORLD)");
            this.addItem("IP認證(IP)");
        } else if (locale == Locale.ENGLISH) {
            this.addItem("WORLD");
            this.addItem("DIGEST");
            this.addItem("IP");
        }
        return this.getItems();
    }
}
