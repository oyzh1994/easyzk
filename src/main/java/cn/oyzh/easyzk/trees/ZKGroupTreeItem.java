package cn.oyzh.easyzk.trees;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoAddController;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * zk分组节点
 *
 * @author oyzh
 * @since 2023/05/12
 */
@Slf4j
public class ZKGroupTreeItem extends ZKTreeItem<ZKGroupTreeItemValue> implements ZKConnectManager {

    /**
     * 分组对象
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private final ZKGroup value;

    /**
     * zk信息储存
     */
    private final ZKInfoStore infoStore = ZKInfoStore.INSTANCE;

    /**
     * zk分组储存
     */
    private final ZKGroupStore groupStore = ZKGroupStore.INSTANCE;

    public ZKGroupTreeItem(@NonNull ZKGroup group, @NonNull ZKTreeView treeView) {
        super(treeView);
        this.value = group;
        this.setValue(new ZKGroupTreeItemValue(this));
        // this.itemValue(group.getName());
//        this.getChildren().addListener((ListChangeListener<? super ZKConnectTreeItem>) c -> {
//            // this.getTreeView().fireChildChanged();
//            ZKEventUtil.treeChildChanged();
//            this.getTreeView().flushLocal();
//        });
        // 监听节点变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            ZKEventUtil.treeChildChanged();
//            this.flushLocal();
        });
        // 监听展开变化
        this.expandedProperty().addListener((observable, oldValue, newValue) -> {
            this.value.setExpand(newValue);
            this.groupStore.update(this.value);
        });
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem addConnect = MenuItemExt.newItem("添加连接", new SVGGlyph("/font/add.svg", "12"), "添加zk连接", this::addConnect);
        MenuItem renameGroup = MenuItemExt.newItem("分组更名", new SVGGlyph("/font/edit-square.svg", "12"), "更改分组名称(快捷键f2)", this::rename);
        MenuItem delGroup = MenuItemExt.newItem("删除分组", new SVGGlyph("/font/delete.svg", "12"), "删除此分组", this::delete);

        items.add(addConnect);
        items.add(renameGroup);
        items.add(delGroup);
        return items;
    }

    @Override
    public void rename() {
        String groupName = MessageBox.prompt("请输入新的分组名称", this.value.getName());

        // 名称为null或者跟当前名称相同，则忽略
        if (groupName == null || Objects.equals(groupName, this.value.getName())) {
            return;
        }

        // 检查名称
        if (StrUtil.isBlank(groupName)) {
            MessageBox.warn("分组名称不能为空！");
            return;
        }

        // 检查是否存在
        String name = this.value.getName();
        this.value.setName(groupName);
        if (this.groupStore.exist(this.value)) {
            this.value.setName(name);
            MessageBox.warn("此分组已存在！");
            return;
        }

        // 修改名称
        if (this.groupStore.update(this.value)) {
            this.getValue().flushText();
        } else {
            MessageBox.warn("修改分组名称失败！");
        }
    }

    @Override
    public void delete() {
        if (this.isChildEmpty() && !MessageBox.confirm("确定删除此分组？")) {
            return;
        }
        if (!this.isChildEmpty() && !MessageBox.confirm("确定删除此分组？(连接将移动到根节点)")) {
            return;
        }

        // 删除失败
        if (!this.groupStore.delete(this.value)) {
            MessageBox.warn("删除分组失败！");
            return;
        }

        // 处理连接
        if (!this.isChildEmpty()) {
            // 清除分组id
            List<ZKConnectTreeItem> childes = this.getChildren();
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
        StageWrapper fxView = StageUtil.parseStage(ZKInfoAddController.class, this.parent().window());
        fxView.setProp("group", this.value);
        fxView.display();
    }

    /**
     * 父节点
     *
     * @return zk根节点
     */
    public ZKRootTreeItem parent() {
        Object parent = this.getParent();
        return (ZKRootTreeItem) parent;
    }

    @Override
    public void addConnect(@NonNull ZKInfo zkInfo) {
        super.addChild(new ZKConnectTreeItem(zkInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem(@NonNull ZKConnectTreeItem item) {
        if (this.getChildren().contains(item)) {
            return;
        }
        if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
            item.value().setGroupId(this.value.getGid());
            this.infoStore.update(item.value());
        }
        super.addChild(item);
        if (!this.isExpanded()) {
            this.extend();
        }
    }

    @Override
    public void addConnectItems(@NonNull List<ZKConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.getChildren().addAll(items);
            this.sort();
            // this.sort(this.getTreeView().sortOrder());
        }
    }

    @Override
    public boolean delConnectItem(@NonNull ZKConnectTreeItem item) {
        // 删除连接
        if (this.infoStore.delete(item.value())) {
            this.removeChild(item);
            return true;
        }
        return false;
    }

    @Override
    public List<ZKConnectTreeItem> getConnectItems() {
        return this.getChildren();
    }

    @Override
    public List<ZKConnectTreeItem> getConnectedItems() {
        List<ZKConnectTreeItem> items = new ArrayList<>(this.getChildren().size());
        for (Object item : this.getChildren()) {
            if (item instanceof ZKConnectTreeItem treeItem) {
                if (treeItem.isConnect()) {
                    items.add(treeItem);
                }
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

    public List<ZKConnectTreeItem> children() {
        List<ZKConnectTreeItem> list=new ArrayList<>();
        for (Object child : this.getChildren()) {
            if(child instanceof ZKConnectTreeItem treeItem){
                list.add(treeItem);
            }
        }
        return list;
    }
}
