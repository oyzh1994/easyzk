package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.fx.plus.controls.box.FlexVBox;

/**
 * zk权限VBox
 *
 * @author oyzh
 * @since 2022/06/05
 */
public class ZKACLVBox extends FlexVBox {

    public void acl(ZKACL acl) {
        this.setUserData(acl);
    }

    public ZKACL acl() {
        return (ZKACL) this.getUserData();
    }
}
