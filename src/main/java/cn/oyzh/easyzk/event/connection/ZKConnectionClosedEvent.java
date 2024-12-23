package cn.oyzh.easyzk.event.connection;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKConnectionClosedEvent extends Event<ZKClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s:%s closed] ", I18nHelper.connect(), this.data().connectName());
    }

    public ZKConnect connect() {
        return this.data().zkConnect();
    }
}
