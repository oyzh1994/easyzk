package cn.oyzh.easyzk.trees.group;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easyzk.controller.info.ZKInfoAddController;
import cn.oyzh.easyzk.domain.ZKGroup;
import cn.oyzh.easyzk.domain.ZKInfo;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.easyzk.store.ZKGroupStore;
import cn.oyzh.easyzk.store.ZKInfoStore;
import cn.oyzh.easyzk.trees.ZKConnectManager;
import cn.oyzh.easyzk.trees.ZKTreeItem;
import cn.oyzh.easyzk.trees.ZKTreeView;
import cn.oyzh.easyzk.trees.connect.ZKConnectTreeItem;
import cn.oyzh.easyzk.trees.root.ZKRootTreeItem;
import cn.oyzh.fx.plus.controls.popup.MenuItemExt;
import cn.oyzh.fx.plus.controls.svg.DeleteSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.EditSVGGlyph;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.information.MessageBox;
import cn.oyzh.fx.plus.stage.StageUtil;
import cn.oyzh.fx.plus.stage.StageWrapper;
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
//@Slf4j
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
        // 判断是否展开
        this.setExpanded(this.value.isExpand());
        // 监听节点变化
        super.addEventHandler(childrenModificationEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> ZKEventUtil.treeChildChanged());
        // 监听收缩变化
        super.addEventHandler(branchCollapsedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(false);
            this.groupStore.update(this.value);
        });
        // 监听展开变化
        super.addEventHandler(branchExpandedEvent(), (EventHandler<TreeModificationEvent<TreeItem<?>>>) event -> {
            this.value.setExpand(true);
            this.groupStore.update(this.value);
        });
    }

    @Override
    public List<MenuItem> getMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        MenuItem addConnect = MenuItemExt.newItem("添加连接", new SVGGlyph("/font/add.svg", "12"), "添加zk连接", this::addConnect);
        MenuItem renameGroup = MenuItemExt.newItem("分组更名", new EditSVGGlyph("12"), "更改分组名称(快捷键f2)", this::rename);
        MenuItem delGroup = MenuItemExt.newItem("删除分组", new DeleteSVGGlyph("12"), "删除此分组", this::delete);
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
        StageWrapper fxView = StageUtil.parseStage(ZKInfoAddController.class, this.window());
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
        this.addConnectItem(new ZKConnectTreeItem(zkInfo, this.getTreeView()));
    }

    @Override
    public void addConnectItem(@NonNull ZKConnectTreeItem item) {
        if (!this.containsChild(item)) {
            if (!Objects.equals(item.value().getGroupId(), this.value.getGid())) {
                item.value().setGroupId(this.value.getGid());
                this.infoStore.update(item.value());
            }
            super.addChild(item);
        }
    }

    @Override
    public void addConnectItems(@NonNull List<ZKConnectTreeItem> items) {
        if (CollUtil.isNotEmpty(items)) {
            this.addChild((List) items);
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
