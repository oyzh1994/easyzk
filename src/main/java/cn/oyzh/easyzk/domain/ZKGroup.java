package cn.oyzh.easyzk.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * zk树分组
 *
 * @author oyzh
 * @since 2023/5/12
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ZKGroup implements Comparable<ZKGroup> {

    /**
     * 分组id
     */
    @Getter
    @Setter
    private String gid;

    /**
     * 分组名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 是否展开分组
     */
    @Getter
    @Setter
    private Boolean expand;

    @Override
    public int compareTo(ZKGroup o) {
        if (o == null) {
            return 1;
        }
        return this.name.compareToIgnoreCase(o.getName());
    }

    /**
     * 是否展开分租
     *
     * @return 结果
     */
    public boolean isExpand() {
        return Boolean.TRUE == this.expand;
    }
}
