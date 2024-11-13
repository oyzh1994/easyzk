package cn.oyzh.easyzk.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * @author oyzh
 * @since 2024-09-26
 */
@Data
@Table("t_collect")
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

    public ZKCollect() {

    }

    public ZKCollect(String iid, String path) {
        this.iid = iid;
        this.path = path;
    }

}
