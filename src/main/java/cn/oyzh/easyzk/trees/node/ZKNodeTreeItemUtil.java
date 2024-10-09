package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.zk.ZKNode;
import lombok.experimental.UtilityClass;

/**
 * @author oyzh
 * @since 2024-10-09
 */
@UtilityClass
public class ZKNodeTreeItemUtil {

    public static ZKNodeTreeItem of(ZKNode node, ZKConnectTreeItem item) {
        if (node.rootNode()) {
            return new ZKRootNodeTreeItem(node, item);
        }
        return new ZKNodeTreeItem(node, item);
    }
}
