package cn.oyzh.easyzk.trees.node;

import cn.oyzh.easyzk.event.TreeChildFilterEvent;
import cn.oyzh.easyzk.util.ZKNodeUtil;
import cn.oyzh.easyzk.zk.ZKClient;
import cn.oyzh.fx.common.log.JulLog;
import cn.oyzh.fx.plus.event.EventListener;
import cn.oyzh.fx.plus.trees.RichTreeView;
import cn.oyzh.fx.plus.util.FXUtil;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    @Getter
    @Setter
    private ZKClient client;

    public ZKNodeTreeView() {
    }

    @Override
    public ZKNodeTreeItemFilter itemFilter() {
        try {
            // 初始化过滤器
            if (this.itemFilter == null) {
                ZKNodeTreeItemFilter filter = new ZKNodeTreeItemFilter();
                filter.initFilters();
                this.itemFilter = filter;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return (ZKNodeTreeItemFilter) this.itemFilter;
    }

    @Override
    public ZKNodeTreeItem getRoot() {
        return (ZKNodeTreeItem) super.getRoot();
    }

    @Override
    public ZKNodeTreeItem getSelectedItem() {
        return (ZKNodeTreeItem) super.getSelectedItem();
    }

    /**
     * 寻找zk节点
     *
     * @param targetPath 目标路径
     * @return zk节点
     */
    public ZKNodeTreeItem findNodeItem(@NonNull String targetPath) {
        return this.findNodeItem(this.getRoot(), targetPath);
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
     * 树节点过滤
     */
    @Subscribe
    private void treeChildFilter(TreeChildFilterEvent event) {
        this.itemFilter().initFilters();
        this.filter();
    }

    @Override
    public void expand() {
        ZKNodeTreeItem item = this.getSelectedItem();
        if (item != null) {
            item.expandAll();
            this.select(item);
        }
    }

    @Override
    public void collapse() {
        ZKNodeTreeItem item = this.getSelectedItem();
        if (item != null) {
            item.collapseAll();
            this.select(item);
        }
    }

    public void onNodeAdd(String nodePath) {
        try {
            String pPath = ZKNodeUtil.getParentPath(nodePath);
            // 寻找节点
            ZKNodeTreeItem parent = this.findNodeItem(pPath);
            // 父节点不存在
            if (parent == null) {
                JulLog.warn("{}: 未找到节点的父节点，无法处理节点！", nodePath);
                return;
            }
            // 获取节点
            ZKNodeTreeItem item = parent.getNodeItem(pPath);
            // 刷新节点
            if (item != null) {
                item.refreshNode();
                JulLog.info("节点已存在, 更新节点.");
            } else if (parent.loaded()) {// 添加节点
                parent.refreshStat();
                parent.addChild(nodePath);
                JulLog.info("节点不存在, 添加节点.");
            } else if (this.client.isLastCreate(nodePath)) {// 加载子节点
                parent.refreshStat();
                parent.loadChild(false);
                parent.flushValue();
                JulLog.info("父节点未加载, 加载父节点.");
            }
            // 过滤节点
            parent.doFilter(this.itemFilter());
            // 选中此节点
            if (this.client.isLastCreate(nodePath)) {
                if (item == null) {
                    item = parent.getNodeItem(nodePath);
                }
                if (item != null) {
                    ZKNodeTreeItem finalItem = item;
                    FXUtil.runPulse(() -> this.select(finalItem));
                }
            }
        } catch (Exception ex) {
            JulLog.warn("新增节点失败！", ex);
        }
    }

    public void onNodeAdded(String nodePath) {
        if (this.client.isLastCreate(nodePath)) {
            return;
        }

    }

    public void onNodeDeleted(String nodePath) {
        if (this.client.isLastDelete(nodePath)) {
            return;
        }
        try {
            // 寻找节点
            ZKNodeTreeItem item = this.findNodeItem(nodePath);
            // 更新信息
            if (item != null) {
                item.setBeDeleted();
            } else {
                JulLog.warn("{}: 未找到被删除节点，无法处理节点！", nodePath);
            }
        } catch (Exception ex) {
            JulLog.warn("删除节点失败！", ex);
        }
    }

    public void onNodeUpdated(String nodePath) {
        if (this.client.isLastUpdate(nodePath)) {
            return;
        }
        try {
            // 寻找节点
            ZKNodeTreeItem item = this.findNodeItem(nodePath);
            // 更新信息
            if (item != null) {
                item.setBeChanged();
            } else {
                JulLog.warn("{}: 未找到被修改节点，无法处理节点！", nodePath);
            }
        } catch (Exception ex) {
            JulLog.warn("修改节点失败！", ex);
        }
    }
}
