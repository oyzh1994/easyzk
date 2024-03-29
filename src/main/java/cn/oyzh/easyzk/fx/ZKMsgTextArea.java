package cn.oyzh.easyzk.fx;

import cn.oyzh.fx.common.Const;
import cn.oyzh.fx.plus.controls.area.MsgTextArea;
import cn.oyzh.fx.plus.event.EventFormatter;
import cn.oyzh.fx.plus.event.EventListener;
import com.google.common.eventbus.Subscribe;

/**
 * @author oyzh
 * @since 2024/3/29
 */
public class ZKMsgTextArea extends MsgTextArea implements EventListener {

    @Subscribe
    private void onEventMsg(EventFormatter formatter) {
        String formatMsg = formatter.eventFormat();
        if (formatMsg != null) {
            this.appendLine(String.format("%s %s", Const.DATE_TIME_FORMAT.format(System.currentTimeMillis()), formatMsg));
        }
    }
}
