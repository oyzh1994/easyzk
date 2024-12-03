package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点过滤器
 *
 * @author oyzh
 * @since 2023/3/28
 */
public class ZKConnectTreeItemFilter implements RichTreeItemFilter {

    /**
     * 排除子节点
     */
    @Setter
    @Getter
    private boolean excludeSub;

    /**
     * 仅看收藏节点
     */
    @Setter
    @Getter
    private boolean onlyCollect;

    /**
     * 排除临时节点
     */
    @Setter
    @Getter
    private boolean excludeEphemeral;

    /**
     * 过滤内容列表
     */
    private final List<ZKFilter> filters = new ArrayList<>();

    /**
     * 过滤配置储存
     */
    private final ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    /**
     * 初始化过滤配置
     */
    public void initFilters() {
        this.filters.clear();
        this.filters.addAll(this.filterStore.loadEnable());
    }

    @Override
    public boolean test(RichTreeItem<?> item) {
        if (item instanceof ZKNodeTreeItem treeItem) {
            ZKNode node = treeItem.value();
            // 根节点直接展示
            if (node.isRoot()) {
                return true;
            }
            // 仅看收藏
            if (this.onlyCollect && !treeItem.isCollect()) {
                return false;
            }
            // 过滤子节点
            if (this.excludeSub && node.isChildren()) {
                return false;
            }
            // 过滤临时节点
            if (this.excludeEphemeral && node.isEphemeral()) {
                return false;
            }
            // 过滤节点
            return !ZKNodeUtil.isFiltered(treeItem.nodePath(), this.filters);
        }
        return true;
    }
}
