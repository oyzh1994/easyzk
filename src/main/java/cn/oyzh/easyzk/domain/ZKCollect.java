package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.sqlite.Column;
import cn.oyzh.fx.common.sqlite.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Data
@Table("t_collect")
@NoArgsConstructor
@AllArgsConstructor
public class ZKCollect implements Serializable {

    /**
     * 信息id
     */
    @Column
    private String iid;

    /**
     * 路径
     */
    @Column
    private String path;

}
