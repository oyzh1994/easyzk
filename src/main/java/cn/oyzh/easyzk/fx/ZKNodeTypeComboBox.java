package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/19
 */
public class ZKNodeTypeComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        this.addItem(I18nHelper.allNode());
        this.addItem(I18nHelper.persistentNode());
        this.addItem(I18nHelper.temporaryNode());
        return this.getItems();
    }
}
