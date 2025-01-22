package cn.oyzh.easyzk.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2024-10-16
 */
public class ParamSVGGlyph extends SVGGlyph {

    public ParamSVGGlyph() {
        super("/font/param.svg");
    }

    public ParamSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}
