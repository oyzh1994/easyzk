package cn.oyzh.easyzk.search;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.i18n.I18nHelper;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/19
 */
public class ZKNodeSearchTypeComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        this.addItem(I18nHelper.allNode());
        this.addItem(I18nHelper.collectNode());
        this.addItem(I18nHelper.persistentNode());
        return this.getItems();
    }
}
