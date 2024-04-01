package cn.oyzh.easyzk.event;

import cn.oyzh.fx.plus.event.Event;
import cn.oyzh.fx.plus.event.EventFormatter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2023/9/18
 */
@Getter
@Accessors(fluent = true)
public class ZKNodeDeleteEvent extends Event<String> implements EventFormatter {

    @Setter
    private String infoName;

    @Setter
    private boolean delChildren;

    @Override
    public String eventFormat() {
        if (this.delChildren) {
            return String.format("[%s] 级联删除节点:%s", this.infoName, this.data());
        }
        return String.format("[%s] 删除节点:%s", this.infoName, this.data());
    }
}
