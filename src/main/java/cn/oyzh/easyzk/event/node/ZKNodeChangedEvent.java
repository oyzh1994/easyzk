package cn.oyzh.easyzk.event.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
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
public class ZKNodeChangedEvent extends Event<String> implements EventFormatter {

    private ZKClient client;

    public ZKConnect connect() {
        return this.client.connect();
    }

    @Override
    public String eventFormat() {
        return String.format("[%s:%s changed, path:%s] ", I18nHelper.connect(), this.connect().getName(), this.data());
    }

}
