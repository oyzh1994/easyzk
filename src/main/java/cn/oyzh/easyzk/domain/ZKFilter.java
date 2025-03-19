package cn.oyzh.easyzk.domain;

import cn.oyzh.common.object.ObjectComparator;
import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.PrimaryKey;
import cn.oyzh.store.jdbc.Table;

import java.io.Serializable;
import java.util.Objects;


/**
 * zk过滤配置
 *
 * @author oyzh
 * @since 2022/12/20
 */
@Table("t_filter")
public class ZKFilter implements ObjectComparator<ZKFilter>, Serializable {

    /**
     * id
     */
    @Column
    @PrimaryKey
    private String uid;

    /**
     * iid
     * @see ZKConnect
     */
    @Column
    private String iid;

    /**
     * 关键词
     */
    @Column
    private String kw;

    /**
     * 是否启用
     */
    @Column
    private boolean enable;

    /**
     * 模糊匹配
     * true 模糊匹配
     * false 完全匹配
     */
    @Column
    private boolean partMatch;

    /**
     * 复制对象
     *
     * @param filter 过滤信息
     * @return 当前对象
     */
    public ZKFilter copy( ZKFilter filter) {
        this.kw = filter.kw;
        this.iid = filter.iid;
        this.uid = filter.uid;
        this.enable = filter.enable;
        this.partMatch = filter.partMatch;
        return this;
    }

    @Override
    public boolean compare(ZKFilter filter) {
        if (this.equals(filter)) {
            return true;
        }
        return Objects.equals(filter.kw, this.kw);
    }

    /**
     * 比较信息
     *
     * @param kw 关键字
     * @return 结果
     */
    public boolean compare(String kw) {
        return Objects.equals(kw, this.kw);
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

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isPartMatch() {
        return partMatch;
    }

    public void setPartMatch(boolean partMatch) {
        this.partMatch = partMatch;
    }
}
