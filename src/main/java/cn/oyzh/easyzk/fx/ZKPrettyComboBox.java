package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/11
 */
public class ZKPrettyComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        this.addItem(I18nHelper.enable());
        this.addItem(I18nHelper.close());
        return this.getItems();
    }
}
