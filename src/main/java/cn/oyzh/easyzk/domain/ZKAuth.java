package cn.oyzh.easyzk.domain;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.util.ZKAuthUtil;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * zk认证信息
 *
 * @author oyzh
 * @since 2022/6/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZKAuth implements ObjectComparator<ZKAuth>, Serializable {

    /**
     * 数据id
     */
    private String uid;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否启用
     */
    private Boolean enable;

    public ZKAuth(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * 生成摘要
     *
     * @return 摘要信息
     */
    public String digest() {
        if (StrUtil.isBlank(this.getUser()) || StrUtil.isBlank(this.getPassword())) {
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
     * 比较信息
     *
     * @param user     认证用户
     * @param password 认证密码
     * @return 结果
     */
    public boolean compare(String user, String password) {
        return this.compare(new ZKAuth(user, password));
    }

    /**
     * 复制认证信息
     *
     * @param auth 认证信息
     * @return 当前认证信息
     */
    public ZKAuth copy(@NonNull ZKAuth auth) {
        this.setUid(auth.uid);
        this.setUser(auth.user);
        this.setEnable(auth.enable);
        this.setPassword(auth.password);
        return this;
    }

    public boolean getEnable() {
        return this.enable == null || this.enable;
    }
}
