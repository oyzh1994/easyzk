package cn.oyzh.easyzk.event.window;

import cn.oyzh.easyzk.dto.ZKACL;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;

/**
 * @author oyzh
 * @since 2025-02-20
 */
public class ZKShowUpdateACLEvent extends Event<ZKNodeTreeItem> {

    private ZKACL acl;

    private ZKClient client;

    public ZKACL getAcl() {
        return acl;
    }

    public void setAcl(ZKACL acl) {
        this.acl = acl;
    }

    public ZKClient getClient() {
        return client;
    }

    public void setClient(ZKClient client) {
        this.client = client;
    }
}
