package cn.oyzh.easyzk.trees;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.fx.plus.controls.FlexImageView;
import cn.oyzh.fx.plus.util.IconUtil;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;


/**
 * zk树节点值
 *
 * @author oyzh
 * @since 2023/4/7
 */
@Slf4j
@Accessors(chain = true, fluent = true)
public class ZKRootTreeItemValue extends ZKTreeItemValue {

    public ZKRootTreeItemValue() {
        this.flushGraphic();
        this.flushText();
    }

    @Override
    public String name() {
        return "ZK连接列表";
    }

    @Override
    public void flushGraphic() {
        if (this.graphic() == null) {
            this.graphic(new FlexImageView(IconUtil.getIcon(ZKConst.ICON_PATH), 14));
        }
    }

    @Override
    public void flushGraphicColor() {

    }
}
