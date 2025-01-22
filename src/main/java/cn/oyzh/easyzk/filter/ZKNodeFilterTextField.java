package cn.oyzh.easyzk.filter;

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

    public ZKNodeFilterParam filterParam() {
        return this.skin().filterParam();
    }

    public byte filterMode() {
        ZKNodeFilterParam filterParam = this.filterParam();
        if (filterParam.isMatchCase() && filterParam.isMatchFull()) {
            return 3;
        }
        if (filterParam.isMatchFull()) {
            return 2;
        }
        if (filterParam.isMatchCase()) {
            return 1;
        }
        return 0;
    }

    public byte filterScope() {
        ZKNodeFilterParam filterParam = this.filterParam();
        if (filterParam.isSearchData() && filterParam.isSearchPath()) {
            return 2;
        }
        if (filterParam.isSearchData()) {
            return 1;
        }
        return 0;
    }
}
