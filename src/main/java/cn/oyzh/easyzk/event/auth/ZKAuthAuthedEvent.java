package cn.oyzh.easyzk.event.auth;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class ZKAuthAuthedEvent extends Event<ZKNodeTreeItem> {

    private String user;

    private String password;

    private boolean success;

    public ZKAuth auth() {
        return new ZKAuth(this.data().client().iid(), this.user, this.password);
    }

    public ZKClient client() {
        return this.data().client();
    }

}
