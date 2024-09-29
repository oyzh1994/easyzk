package cn.oyzh.easyzk.trees.group;

import cn.oyzh.easyzk.controller.info.ZKInfoAddController;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKGroupStore2;
import cn.oyzh.easyzk.store.ZKInfoStore2;
import cn.oyzh.easyzk.trees.ZKConnectManager;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.root.ZKRootTreeItem;
import cn.oyzh.fx.common.util.CollectionUtil;
import cn.oyzh.fx.common.util.StringUtil;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.menu.FXMenuItem;
import cn.oyzh.fx.plus.menu.MenuItemHelper;
import cn.oyzh.fx.plus.window.StageAdapter;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * zk分组节点
 *
 * @author oyzh
 * @since 2023/05/12
 */
public class ZKGroupTreeItem extends ZKTreeItem<ZKGroupTreeItemValue> implements ZKConnectManager {

    /**
     * 分组对象
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final ZKGroup value;

    // /**
    //  * zk信息储存
    //  */
    // private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    /**
     * zk分组储存
     */
    private final ZKGroupStore2 groupStore = ZKGroupStore2.INSTANCE;

    public ZKGroupTreeItem(@NonNull ZKGroup group, @NonNull ZKTreeView treeView) {
        super(treeView);
        this.value = group;
        this.setValue(new ZKGroupTreeItemValue(this));
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        // 监听变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            ZKEventUtil.treeChildChanged();
            this.flushLocal();
        });
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            if (this.value.isExpand()) {
                this.value.setExpand(false);
                this.groupStore.replace(this.value);
            }
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            if (!this.isExpanded()) {
                this.value.setExpand(true);
                this.groupStore.replace(this.value);
            }
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        FXMenuItem addConnect = MenuItemHelper.addConnect("12", this::addConnect);
        FXMenuItem renameGroup = MenuItemHelper.renameGroup("12", this::rename);
        FXMenuItem delGroup = MenuItemHelper.deleteGroup("12", this::delete);
        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt(I18nHelper.contentTip1(), this.value.getName());
        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }

        // 检查名称
        if (StringUtil.isBlank(groupName)) {
            return;
        }

        // 检查是否存在
        String name = this.value.getName();
        if (this.groupStore.exist(name)) {
            this.value.setName(name);
            MessageBox.warn(I18nHelper.groupAlreadyExists());
            return;
        }

        // 修改名称
        this.value.setName(groupName);
        if (this.groupStore.replace(this.value)) {
            this.getValue().flushText();
        } else {
            MessageBox.warn(I18nHelper.operationFail());
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip1())) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm(I18nHelper.deleteGroupTip2())) {
            return;
        }
        // 删除失败
        if (!this.groupStore.delete(this.value.getName())) {
            MessageBox.warn(I18nHelper.operationFail());
            return;
        }
        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<ZKConnectTreeItem> childes = this.getConnectItems();
            childes.forEach(c -> c.value().setGroupId(null));
            // 连接转移到父节点
            this.parent().addConnectItems(childes);
        }
        // 移除节点
        this.remove();
    }

    /**
     * 添加连接
     */
    private void addConnect() {
        StageAdapter fxView = StageManager.parseStage(ZKInfoAddController.class, this.window());
        fxView.setProp("group", this.value);
        fxView.display();
    }

    /**
     * 父节点
     *
     * @return 根节点
     */
    public ZKRootTreeItem parent() {
        TreeItem<?> treeItem = this.getParent();
        return (ZKRootTreeItem) treeItem;
    }

    @Override
    public void addConnect(@NonNull ZKInfo zkInfo) {
        this.addConnectItem(new ZKConnectTreeItem(zkInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem(@NonNull ZKConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
                ZKInfoStore2.INSTANCE.replace(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems(@NonNull List<ZKConnectTreeItem> items) {
        if (CollectionUtil.isNotEmpty(items)) {
            this.addChild((List) items);
        }
    }

    @Override
    public boolean delConnectItem(@NonNull ZKConnectTreeItem item) {
        // 删除连接
        if (ZKInfoStore2.INSTANCE.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<ZKConnectTreeItem> getConnectItems() {
        List<ZKConnectTreeItem> items = new ArrayList<>(this.getChildrenSize());
        for (TreeItem<?> item : this.getRealChildren()) {
            if (item instanceof ZKConnectTreeItem treeItem) {
                items.add(treeItem);
            }
        }
        return items;
    }

    @Override
    public boolean allowDrop() {
        return true;
    }

    @Override
    public boolean allowDropNode(DragNodeItem item) {
        if (item instanceof ZKConnectTreeItem connectTreeItem) {
            return !Objects.equals(connectTreeItem.value().getGroupId(), this.value.getGid());
        }
        return false;
    }

    @Override
    public void onDropNode(DragNodeItem item) {
        if (item instanceof ZKConnectTreeItem connectTreeItem) {
            connectTreeItem.remove();
            this.addConnectItem(connectTreeItem);
        }
    }
}
