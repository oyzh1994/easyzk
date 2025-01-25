package cn.oyzh.easyzk.domain;

import cn.oyzh.ssh.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * zk连接ssh配置
 *
 * @author oyzh
 * @since 2024-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("t_ssh_config")
public class ZKSSHConfig extends SSHConnect implements Serializable {

    /**
     * 连接id
     *
     * @see ZKConnect
     */
    @Column
    @PrimaryKey
    private String iid;
}
