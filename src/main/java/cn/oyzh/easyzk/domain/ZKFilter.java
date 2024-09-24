package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.sqlite.Column;
import cn.oyzh.fx.common.sqlite.PrimaryKey;
import cn.oyzh.fx.common.sqlite.Table;
import cn.oyzh.fx.common.util.ObjectComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Objects;


/**
 * zk过滤配置
 *
 * @author oyzh
 * @since 2022/12/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("t_filter")
public class ZKFilter implements ObjectComparator<ZKFilter>, Serializable {

    /**
     * id
     */
    @Column
    @PrimaryKey
    private String uid;

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
    public ZKFilter copy(@NonNull ZKFilter filter) {
        this.kw = filter.kw;
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
}
