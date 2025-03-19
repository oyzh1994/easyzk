package cn.oyzh.easyzk.event.auth;

import cn.oyzh.easyzk.domain.ZKAuth;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.event.Event;
import cn.oyzh.event.EventFormatter;
import cn.oyzh.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2023/9/18
 */
public class ZKAuthAuthedEvent extends Event<ZKNodeTreeItem> implements EventFormatter {

    private String user;

    private String password;

    private boolean success;

    public ZKAuth auth() {
        return new ZKAuth(this.data().client().iid(), this.user, this.password);
    }

    public ZKClient client() {
        return this.data().client();
    }

    @Override
    public String eventFormat() {
        return String.format(
                "[%s:%s authed %s, user:%s password:%s] ",
                I18nHelper.connect(), this.data().connectName(), this.success ? I18nHelper.success() : I18nHelper.fail(), this.user, this.password
        );
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
