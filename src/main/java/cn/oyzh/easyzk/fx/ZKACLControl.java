package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.fx.plus.controls.text.FXText;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

/**
 * zk权限
 *
 * @author oyzh
 * @since 2022/6/7
 */

public class ZKACLControl extends ZKACL {

    /**
     * 是否已认证
     */
    @Getter
    @Setter
    private boolean authed;

    /**
     * 是否友好
     */
    @Setter
    @Getter
    private boolean friendly;

    public String getIdControl() {
        return (String) super.idFriend().getValue(this.friendly);
    }

    public String getPermsControl() {
        return (String) super.permsFriend().getValue(this.friendly);
    }

    public String getSchemaControl() {
        return (String) super.schemeFriend().getValue(this.friendly);
    }

    public FXText getStatusControl() {
        if (this.authed) {
            FXText text = new FXText();
            text.setFill(Color.FORESTGREEN);
            text.setText(I18nHelper.authed());
            return text;
        }
        return null;
    }
}
