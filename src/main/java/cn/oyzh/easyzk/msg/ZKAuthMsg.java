package cn.oyzh.easyzk.msg;

import cn.oyzh.easyzk.fx.ZKNodeTreeItem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKAuthMsg {

    @Setter
    private String user;

    @Setter
    private String password;

    @Setter
    private boolean result;

    @Setter
    private ZKNodeTreeItem item;
}
