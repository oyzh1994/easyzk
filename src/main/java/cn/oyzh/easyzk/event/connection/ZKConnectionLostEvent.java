package cn.oyzh.easyzk.event.connection;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.fx.plus.i18n.I18nResourceBundle;

/**
 * @author oyzh
 * @since 2023/9/19
 */
public class ZKConnectionLostEvent extends Event<ZKClient> implements EventFormatter {

    @Override
    public String eventFormat() {
        return String.format("[%s] " + I18nResourceBundle.i18nString("base.client", "base.connectLoss"), this.data().connectName());
    }

    public ZKConnect connect() {
        return this.data().connect();
    }
}
