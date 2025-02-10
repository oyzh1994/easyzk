package cn.oyzh.easyzk.fx.svg.glyph;

import cn.oyzh.fx.plus.controls.svg.SVGGlyph;

/**
 * @author oyzh
 * @since 2025-01-23
 */
public class NodeSVGGlyph extends SVGGlyph {

    public NodeSVGGlyph() {
        super("/font/file-text.svg");
    }

    public NodeSVGGlyph(String size) {
        this();
        this.setSizeStr(size);
    }
}
