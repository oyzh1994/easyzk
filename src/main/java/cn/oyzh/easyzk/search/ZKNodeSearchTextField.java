package cn.oyzh.easyzk.search;

import cn.oyzh.fx.gui.text.field.LimitTextField;
import cn.oyzh.fx.plus.event.AnonymousEvent;
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
        // this.setPromptText(I18nHelper.contains());
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
    public ZKNodeSearchTextFieldSkin skin() {
        return (ZKNodeSearchTextFieldSkin) this.getSkin();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ZKNodeSearchTextFieldSkin(this) {
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
