package cn.oyzh.easyzk.search;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.Hyperlink;

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
        this.addItem(I18nHelper.allNodes());
        this.addItem(I18nHelper.collectNodes());
        this.addItem(I18nHelper.persistentNodes());
        this.addItem(I18nHelper.temporaryNodes());
        return this.getItems();
    }
}
