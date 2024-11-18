package cn.oyzh.easyzk.domain;

import cn.oyzh.ssh.SSHConnect;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Data
@Table("t_ssh_info")
@EqualsAndHashCode(callSuper = true)
public class ZKSSHConnect extends SSHConnect implements Serializable {

    @Getter
    @Setter
    @Column
    @PrimaryKey
    private String iid;
}
