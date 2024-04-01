package cn.oyzh.easyzk.event;

import cn.oyzh.easyzk.domain.ZKInfo;
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
public class ZKNodeAddEvent extends Event<String> implements EventFormatter {

    private ZKInfo info;

    @Override
    public String eventFormat() {
        return String.format("[%s] 新增节点:%s", this.info.getName(), this.data());
    }

}
