package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/11
 */
public class ZKCreateModeComboBox extends FlexComboBox<String> implements I18nSelectAdapter<String> {

    {
        NodeManager.init(this);
    }

    @Override
    public List<String> values(Locale locale) {
        this.clearItems();
        if (locale == Locale.TRADITIONAL_CHINESE) {
            this.addItem("持久节点(PERSISTENT)");
            this.addItem("临时节点(EPHEMERAL)");
            this.addItem("持久顺序节点(PERSISTENT SEQUENTIAL)");
            this.addItem("临时顺序节点(EPHEMERAL SEQUENTIAL)");
            this.addItem("容器节点(CONTAINER)");
        } else if (locale == Locale.SIMPLIFIED_CHINESE) {
            this.addItem("持久节点(PERSISTENT)");
            this.addItem("临时节点(EPHEMERAL)");
            this.addItem("持久顺序节点(PERSISTENT SEQUENTIAL)");
            this.addItem("临时顺序节点(EPHEMERAL SEQUENTIAL)");
            this.addItem("容器节点(CONTAINER)");
        } else {
            this.addItem("PERSISTENT");
            this.addItem("EPHEMERAL");
            this.addItem("PERSISTENT SEQUENTIAL");
            this.addItem("EPHEMERAL SEQUENTIAL");
            this.addItem("CONTAINER");
        }
        return this.getItems();
    }
}
