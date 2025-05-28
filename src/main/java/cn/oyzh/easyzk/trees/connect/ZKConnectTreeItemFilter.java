package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemFilter;

/**
 * 连接节点过滤器
 *
 * @author oyzh
 * @since 2025/05/28
 */
public class ZKConnectTreeItemFilter implements RichTreeItemFilter {

    /**
     * 关键字
     */
    private String kw;

    @Override
    public boolean test(RichTreeItem<?> item) {
        if (StringUtil.isNotBlank(this.kw) && item instanceof ZKConnectTreeItem treeItem) {
            return StringUtil.containsIgnoreCase(treeItem.connectName(), this.kw);
        }
        return true;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }
}
