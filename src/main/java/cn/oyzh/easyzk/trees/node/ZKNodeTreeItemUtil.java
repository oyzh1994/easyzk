package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import lombok.experimental.UtilityClass;

/**
 * @author oyzh
 * @since 2024-10-09
 */
@UtilityClass
public class ZKNodeTreeItemUtil {

    public static ZKNodeTreeItem of(ZKNode node, ZKNodeTreeView treeView, ZKClient client) {
        if (node.rootNode()) {
            return new ZKRootNodeTreeItem(node, treeView, client);
        }
        return new ZKNodeTreeItem(node, treeView, client);
    }
}
