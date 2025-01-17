package cn.oyzh.easyzk.fx.filter;

import cn.oyzh.fx.gui.text.field.LimitTextField;
import cn.oyzh.fx.plus.event.AnonymousEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Skin;
import lombok.Getter;
import lombok.Setter;

/**
 * 过滤文本域
 *
 * @author oyzh
 * @since 2023/10/24
 */
public class ZKNodeFilterTextField extends LimitTextField {

    {
        this.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.skin().setSelectedIndex(0);
            }
        });
    }

    @Setter
    @Getter
    private EventHandler<AnonymousEvent<Object>> onSearch;

    /**
     * 当前皮肤
     *
     * @return 皮肤
     */
    public ZKNodeFilterTextFieldSkin skin() {
        return (ZKNodeFilterTextFieldSkin) this.getSkin();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ZKNodeFilterTextFieldSkin(this) {
            @Override
            public void onSearch(String text) {
                super.onSearch(text);
                if (onSearch != null) {
                    onSearch.handle(AnonymousEvent.of(text));
                }
            }
        };
    }

    public int getSelectedIndex() {
        return this.skin().getSelectedIndex();
    }
}
