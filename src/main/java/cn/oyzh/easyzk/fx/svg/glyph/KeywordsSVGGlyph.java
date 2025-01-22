package cn.oyzh.easyzk.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2024-10-16
 */
public class KeywordsSVGGlyph extends SVGGlyph {

    public KeywordsSVGGlyph() {
        super("/font/keywords.svg");
    }

    public KeywordsSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}
