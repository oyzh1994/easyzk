package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.store.ZKFilterJdbcStore;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.common.util.StringUtil;
import cn.oyzh.fx.gui.tree.view.RichTreeItem;
import cn.oyzh.fx.gui.tree.view.RichTreeItemFilter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 树节点过滤器
 *
 * @author oyzh
 * @since 2023/3/28
 */
public class ZKNodeTreeItemFilter implements RichTreeItemFilter {

    /**
     * 0. 所有节点
     * 1. 收藏节点
     * 2. 持久节点
     * 3. 临时节点
     */
    @Setter
    @Getter
    private byte type;

    /**
     * 关键字
     */
    @Getter
    @Setter
    private String kw;

    /**
     * 0. 包含
     * 1. 包含 + 大小写符合
     * 2. 全字匹配
     * 3. 全字匹配 + 大小写符合
     */
    @Getter
    @Setter
    private byte matchMode;

    /**
     * 过滤内容列表
     */
    private List<ZKFilter> filters;

    /**
     * 过滤配置储存
     */
    private final ZKFilterJdbcStore filterStore = ZKFilterJdbcStore.INSTANCE;

    /**
     * 初始化过滤配置
     */
    public void initFilters() {
        this.filters = this.filterStore.loadEnable();
    }

    @Override
    public boolean test(RichTreeItem<?> item) {
        // 根节点直接展示
        if (item instanceof ZKNodeTreeItem treeItem) {
            // 根节点不参与过滤
            if(treeItem.isRoot()){
                return true;
            }
            // 仅收藏
            if (1 == this.type && !treeItem.isCollect()) {
                return false;
            }
            // 仅持久节点
            if (2 == this.type && treeItem.isEphemeral()) {
                return false;
            }
            // 仅临时节点
            if (3 == this.type && !treeItem.isEphemeral()) {
                return false;
            }
            String nodePath = treeItem.decodeNodePath();
            // 过滤节点
            if (ZKNodeUtil.isFiltered(nodePath, this.filters)) {
                return false;
            }
            // 关键字
            if (StringUtil.isNotBlank(this.kw)) {
                if (this.matchMode == 0) {
                    return StringUtil.containsIgnoreCase(nodePath, this.kw);
                }
                if (this.matchMode == 1) {
                    return StringUtil.contains(nodePath, this.kw);
                }
                if (this.matchMode == 2) {
                    return StringUtil.equalsIgnoreCase(nodePath, this.kw);
                }
                if (this.matchMode == 3) {
                    return StringUtil.equals(nodePath, this.kw);
                }
            }
        }
        return true;
    }
}
