package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.domain.ZKSetting;
import cn.oyzh.easyzk.event.TreeChildFilterEvent;
import cn.oyzh.easyzk.store.ZKSettingStore2;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeItemFilter;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.trees.RichTreeView;
import com.google.common.eventbus.Subscribe;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * zk树
 *
 * @author oyzh
 * @since 2023/1/29
 */
@Accessors(chain = true, fluent = true)
public class ZKNodeTreeView extends RichTreeView implements EventListener {

    /**
     * 搜索中标志位
     */
    @Getter
    private volatile boolean searching;

    /**
     * 配置储存对象
     */
    private final ZKSetting setting = ZKSettingStore2.SETTING;

    public ZKNodeTreeView() {
    }

    @Override
    public ZKTreeItemFilter itemFilter() {
        try {
            // 初始化过滤器
            if (this.itemFilter == null) {
                ZKTreeItemFilter filter = new ZKTreeItemFilter();
                filter.initFilters();
                this.itemFilter = filter;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ZKTreeItemFilter) this.itemFilter;
    }

    @Override
    public ZKNodeTreeItem getRoot() {
        return (ZKNodeTreeItem) super.getRoot();
    }

    /**
     * 寻找zk节点
     *
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull String targetPath) {
        return findNodeItem(this.getRoot(), targetPath);
    }

    /**
     * 寻找zk节点
     *
     * @param root       根节点
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull ZKNodeTreeItem root, @NonNull String targetPath) {
        // 节点对应，返回数据
        if (targetPath.equals(root.nodePath())) {
            return root;
        }
        // 互相不包含，返回null
        if (!targetPath.contains(root.nodePath()) && !root.nodePath().startsWith(targetPath)) {
            return null;
        }
        // 子节点为空，返回null
        if (root.isChildEmpty()) {
            return null;
        }
        // 遍历子节点，寻找匹配节点
        for (ZKNodeTreeItem item : root.showChildren()) {
            ZKNodeTreeItem treeItem = this.findNodeItem(item, targetPath);
            // 返回节点信息
            if (treeItem != null) {
                return treeItem;
            }
        }
        return null;
    }

    /**
     * 获取所有zk节点
     *
     * @return zk节点集合
     */
    public List<ZKNodeTreeItem> getAllNodeItem() {
        return null;
    }

    /**
     * 树节点过滤
     */
    @Subscribe
    private void treeChildFilter(TreeChildFilterEvent event) {
        this.itemFilter().initFilters();
        this.filter();
    }

    @Override
    public void expand() {
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof ZKNodeTreeItem treeItem) {
            treeItem.expandAll();
        } else if (item instanceof ZKConnectTreeItem treeItem) {
            treeItem.extend();
            if (!treeItem.isChildEmpty()) {
                treeItem.firstChild().expandAll(); // 展开第一个子项的所有子项
            }
        } else if (item instanceof ZKTreeItem<?> treeItem) {
            treeItem.extend();
        }
        if (item != null) {
            this.select(item);
        }
    }

    @Override
    public void collapse() {
        TreeItem<?> item = this.getSelectedItem();
        if (item instanceof ZKNodeTreeItem treeItem) {
            treeItem.collapseAll();
        } else if (item instanceof ZKConnectTreeItem treeItem) {
            treeItem.collapse();
            if (!treeItem.isChildEmpty()) {
                treeItem.firstChild().collapseAll();
            }
        } else if (item instanceof ZKTreeItem<?> treeItem) {
            treeItem.collapse();
        }
        if (item != null) {
            this.select(item);
        }
    }


}
