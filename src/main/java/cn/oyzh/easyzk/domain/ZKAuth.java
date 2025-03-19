package cn.oyzh.easyzk.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;
import java.util.Objects;

/**
 * zk认证信息
 *
 * @author oyzh
 * @since 2022/6/9
 */
@Table("t_auth")
public class ZKAuth implements ObjectComparator<ZKAuth>, Serializable {

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String uid;

    /**
     * iid
     *
     * @see ZKConnect
     */
    @Column
    private String iid;

    /**
     * 用户名
     */
    @Column
    private String user;

    /**
     * 密码
     */
    @Column
    private String password;

    /**
     * 是否启用
     */
    @Column
    private Boolean enable;

    public ZKAuth() {
    }

    public ZKAuth(String iid, String user, String password) {
        this.iid = iid;
        this.user = user;
        this.password = password;
    }

    /**
     * 生成摘要
     *
     * @return 摘要信息
     */
    public String digest() {
        if (StringUtil.isBlank(this.getUser()) || StringUtil.isBlank(this.getPassword())) {
            return "";
        }
        return ZKAuthUtil.digest(this.getUser(), this.getPassword());
    }

    @Override
    public boolean compare(ZKAuth auth) {
        if (auth == null) {
            return false;
        }
        if (Objects.equals(this, auth)) {
            return true;
        }
        if (!Objects.equals(auth.user, this.user)) {
            return false;
        }
        return Objects.equals(auth.password, this.password);
    }

    /**
     * 复制认证信息
     *
     * @param auth 认证信息
     * @return 当前认证信息
     */
    public ZKAuth copy( ZKAuth auth) {
        this.iid = auth.iid;
        this.uid = auth.uid;
        this.user = auth.user;
        this.enable = auth.enable;
        this.password = auth.password;
        return this;
    }

    public boolean getEnable() {
        return this.isEnable();
    }

    public boolean isEnable() {
        return this.enable == null || this.enable;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
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

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
