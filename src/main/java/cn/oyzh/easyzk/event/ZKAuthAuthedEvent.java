package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
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

    private boolean success;

    private String password;

    public ZKAuth auth() {
        return new ZKAuth(this.user, this.password);
    }

}
