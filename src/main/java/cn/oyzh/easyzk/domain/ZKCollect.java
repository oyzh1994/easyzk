package cn.oyzh.easyzk.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;

/**
 * zk收藏
 *
 * @author oyzh
 * @since 2024-09-26
 */
@Table("t_collect")
public class ZKCollect implements Serializable {

    /**
     * 连接id
     *
     * @see ZKConnect
     */
    @Column
    private String iid;

    /**
     * 路径
     */
    @Column
    private String path;

    public ZKCollect(String iid, String path) {
        this.iid = iid;
        this.path = path;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
