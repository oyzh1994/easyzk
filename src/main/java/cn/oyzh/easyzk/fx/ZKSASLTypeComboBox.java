package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nSelectAdapter;
import cn.oyzh.fx.plus.node.NodeManager;

import java.util.List;
import java.util.Locale;

/**
 * @author oyzh
 * @since 2024/4/23
 */
public class ZKSASLTypeComboBox extends FlexComboBox<String>  {

    {
        NodeManager.init(this);
        this.addItem("Digest");
        // this.addItem("Kerberos");
    }

}
