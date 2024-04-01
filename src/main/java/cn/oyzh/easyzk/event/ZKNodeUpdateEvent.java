package cn.oyzh.easyzk.event;

import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Data
@Accessors(fluent = true)
public class ZKNodeUpdateEvent extends Event<String> implements EventFormatter {

    private String infoName;

    @Override
    public String eventFormat() {
        return String.format("[%s] 修改节点:%s", this.infoName, this.data());
    }
}
