package cn.oyzh.easyzk.event.client;

import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author oyzh
 * @since 2024-12-20
 */
public class ZKClientActionEvent extends Event<String> implements EventFormatter {

    @Setter
    @Accessors(fluent = true, chain = false)
    private String action;

    @Setter
    @Accessors(fluent = true, chain = false)
    private String params;

    private Object actionData;

    public void actionData(Object actionData) {
        if (actionData instanceof String s) {
            if (s.length() > 1024) {
                this.actionData = I18nHelper.dataTooLarge();
            } else {
                this.actionData = s;
            }
        } else if (actionData instanceof byte[] bytes) {
            if (bytes.length > 1024) {
                this.actionData = I18nHelper.dataTooLarge();
            } else {
                this.actionData = new String(bytes);
            }
        } else if (actionData instanceof Number n) {
            this.actionData = n.doubleValue();
        }
    }

    @Override
    public String eventFormat() {
        if (this.params != null && this.actionData != null) {
            return String.format("%s > %s %s %s", this.data(), this.action, this.params, this.actionData);
        }
        if (this.params != null) {
            return String.format("%s > %s %s", this.data(), this.action, this.params);
        }
        return String.format("%s > %s", this.data(), this.action);
    }
}
