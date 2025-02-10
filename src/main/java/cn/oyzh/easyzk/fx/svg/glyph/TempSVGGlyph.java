package cn.oyzh.easyzk.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2025-01-23
 */
public class TempSVGGlyph extends SVGGlyph {

    public TempSVGGlyph() {
        super("/font/temp.svg");
    }

    public TempSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}
