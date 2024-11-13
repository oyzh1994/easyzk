package cn.oyzh.easyzk.domain;

import cn.oyzh.store.jdbc.Column;
import cn.oyzh.store.jdbc.Table;
import cn.oyzh.common.util.ObjectComparator;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * zk搜索历史
 *
 * @author oyzh
 * @since 2023/4/24
 */
@Data
@Table("t_search_history")
public class ZKSearchHistory implements ObjectComparator<ZKSearchHistory>, Serializable {

    /**
     * 词汇
     */
    @Column
    private String kw;

    /**
     * 1 搜索词
     * 2 替换词
     */
    @Column
    private Byte type;

    /**
     * 搜索时间
     */
    @Column
    private long searchTime = System.currentTimeMillis();

    public ZKSearchHistory() {

    }

    public ZKSearchHistory(String kw, byte type) {
        this.kw = kw;
        this.type = type;
    }

    @Override
    public boolean compare(ZKSearchHistory t1) {
        if (t1 == null) {
            return false;
        }
        if (Objects.equals(this, t1)) {
            return true;
        }
        if (!Objects.equals(this.kw, t1.kw)) {
            return false;
        }
        return Objects.equals(this.type, t1.type);
    }
}
