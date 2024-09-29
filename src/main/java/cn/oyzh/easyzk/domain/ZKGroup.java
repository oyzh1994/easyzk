package cn.oyzh.easyzk.domain;

import cn.oyzh.fx.common.jdbc.Table;
import cn.oyzh.fx.common.util.ObjectComparator;
import cn.oyzh.fx.plus.domain.TreeGroup;

/**
 * zk树分组
 *
 * @author oyzh
 * @since 2023/5/12
 */
@Table("t_group")
public class ZKGroup extends TreeGroup implements ObjectComparator<ZKGroup> {

    public ZKGroup() {
        super();
    }

    public ZKGroup(String name, String groupId, boolean expand) {
        super(name, groupId, expand);
    }

    @Override
    public boolean compare(ZKGroup t1) {
        if (t1 == null) {
            return false;
        }
        return StringUtil.equals(this.getName(), t1.getName());
    }
}

