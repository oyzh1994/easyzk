package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.node.NodeManager;

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
