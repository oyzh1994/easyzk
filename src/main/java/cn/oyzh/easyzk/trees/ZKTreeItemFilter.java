package cn.oyzh.easyzk.trees;

import cn.oyzh.easyzk.domain.ZKFilter;
import cn.oyzh.easyzk.search.ZKSearchHandler;
import cn.oyzh.easyzk.search.ZKSearchParam;
import cn.oyzh.easyzk.store.ZKAuthStore2;
import cn.oyzh.easyzk.store.ZKFilterStore2;
import cn.oyzh.easyzk.trees.node.ZKNodeTreeItem;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKNode;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点过滤器
 *
 * @author oyzh
 * @since 2023/3/28
 */
@Lazy
@Component
public class ZKTreeItemFilter implements RichTreeItemFilter {

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
     * zk主页搜索处理
     */
    @Autowired
    private ZKSearchHandler searchHandler;

    /**
     * 过滤内容列表
     */
    private final List<ZKFilter> filters = new ArrayList<>();

    /**
     * 过滤配置储存
     */
    private final ZKFilterStore2 filterStore = ZKFilterStore2.INSTANCE;

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
            if (node.rootNode()) {
                return true;
            }
            // 仅看收藏
            if (this.onlyCollect && !treeItem.isCollect()) {
                return false;
            }
            // 过滤子节点
            if (this.excludeSub && node.subNode()) {
                return false;
            }
            // 过滤临时节点
            if (this.excludeEphemeral && node.ephemeral()) {
                return false;
            }
            // 过滤节点
            if (ZKNodeUtil.isFiltered(treeItem.nodePath(), this.filters)) {
                return false;
            }
        }
        // 判断是否满足搜索要求
        ZKSearchParam param = this.searchHandler.searchParam();
        if (param != null && param.isFilterMode() && !param.isEmpty()) {
            return this.searchHandler.getMatchType(item) != null;
        }
        return true;
    }
}
