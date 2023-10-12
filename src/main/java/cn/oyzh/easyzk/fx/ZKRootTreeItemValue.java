package cn.oyzh.easyzk.fx;

import cn.oyzh.easyzk.ZKConst;
import cn.oyzh.fx.plus.controls.FlexImageView;
import cn.oyzh.fx.plus.util.IconUtil;
import lombok.NonNull;
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
public class ZKRootTreeItemValue extends BaseTreeItemValue {

    private final ZKRootTreeItem treeItem;

    public ZKRootTreeItemValue(@NonNull ZKRootTreeItem treeItem) {
        this.treeItem = treeItem;
        this.flushGraphic();
        this.flushName();
    }

    @Override
    public String name() {
        return "ZK连接列表";
    }

    @Override
    public boolean flushGraphic() {
        if (this.graphic() == null) {
            this.graphic(new FlexImageView(IconUtil.getIcon(ZKConst.ICON_PATH), 14));
            // this.treeItem.treeView().fireGraphicChanged(this.treeItem);
            return true;
        }
        return false;
    }

    @Override
    public void flushGraphicColor() {
    }
}
