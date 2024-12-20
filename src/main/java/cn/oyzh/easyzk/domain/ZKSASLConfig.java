package cn.oyzh.easyzk.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-12-20
 */
@Data
@Table("t_sasl_config")
public class ZKSASLConfig implements Serializable {

    /**
     * 数据id
     */
    @Column
    @PrimaryKey
    private String id;

    /**
     * zk连接id
     *
     * @see ZKConnect
     */
    @Column
    private String iid;

    /**
     * sasl类型
     */
    @Column
    private String type;

    /**
     * 用户名
     */
    @Column
    private String userName;

    /**
     * 密码
     */
    @Column
    private String password;

    public boolean checkInvalid() {
        if ("Digest".equalsIgnoreCase(this.type)) {
            return this.userName == null || this.password == null;
        }
        return false;
    }

}
