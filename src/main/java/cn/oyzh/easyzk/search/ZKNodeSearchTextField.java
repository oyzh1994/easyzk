package cn.oyzh.easyzk.search;

import cn.oyzh.fx.plus.controls.textfield.LimitTextField;
import cn.oyzh.fx.plus.event.AnonymousEvent;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import javafx.event.EventHandler;
import javafx.scene.control.Skin;
import lombok.Getter;
import lombok.Setter;

/**
 * 搜索文本域
 *
 * @author oyzh
 * @since 2023/10/24
 */
public class ZKNodeSearchTextField extends LimitTextField {

    {
        this.setPromptText(I18nHelper.contains());
    }

    /**
     * 当前皮肤
     *
     * @return 皮肤
     */
    public ZKNodeSearchTextFieldSkin skin() {
        return (ZKNodeSearchTextFieldSkin) this.getSkin();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ZKNodeSearchTextFieldSkin(this) ;
    }

    public int getSelectedIndex() {
        return this.skin().getSelectedIndex();
    }
}
