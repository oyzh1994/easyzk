package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.event.Event;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
public class ZKNodeAddedEvent extends Event<ZKNode> {

    private ZKClient client;

    public String nodePath() {
        return this.data().nodePath();
    }

    public String decodeNodePath() {
        return this.data().decodeNodePath();
    }

    public ZKInfo info(){
        return this.client.zkInfo();
    }

}
