package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.jdbc.Column;
import cn.oyzh.fx.common.jdbc.Table;
import cn.oyzh.fx.common.ssh.SSHConnectInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Data
@Table("t_ssh_info")
@EqualsAndHashCode(callSuper = true)
public class ZKSSHInfo extends SSHConnectInfo implements Serializable {

    @Column
    private String iid;

}
