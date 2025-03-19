package cn.oyzh.easyzk.event.node;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKNodeAddedEvent extends Event<String> implements EventFormatter {

    private ZKConnect zkConnect;

    public ZKConnect getZkConnect() {
        return zkConnect;
    }

    public void setZkConnect(ZKConnect zkConnect) {
        this.zkConnect = zkConnect;
    }

    @Override
    public String eventFormat() {
        return String.format("[%s:%s added, path:%s] ", I18nHelper.connect(), this.zkConnect.getName(), this.data());
    }

}
