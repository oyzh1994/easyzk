package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.plus.controls.combo.FXComboBox;
import cn.oyzh.fx.plus.node.NodeManager;

/**
 * @author oyzh
 * @since 2024/4/23
 */
public class ZKSASLTypeComboBox extends FXComboBox<String>  {

    {
        NodeManager.init(this);
        this.addItem("Digest");
        // this.addItem("Kerberos");
    }

}
