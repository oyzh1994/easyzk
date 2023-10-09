package cn.oyzh.easyzk.fx;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.svg.SVGGlyph;
import cn.oyzh.fx.plus.util.FXUtil;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 基础的树节点
 *
 * @author oyzh
 * @since 2023/2/7
 */
@Slf4j
public abstract class BaseTreeItem extends TreeItem implements DragNodeItem {

    /**
     * zk树
     */
    @Setter
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKTreeView treeView;

    /**
     * 开始等待
     */
    public void startWaiting() {
        if (this.itemValue().graphic() instanceof SVGGlyph glyph) {
            glyph.startWaiting();
        }
    }

    /**
     * 开始等待
     *
     * @param task 待执行业务
     */
    public void startWaiting(Task task) {
        if (this.itemValue().graphic() instanceof SVGGlyph glyph) {
            glyph.startWaiting(task);
        }
    }

    /**
     * 取消等待
     */
    public void stopWaiting() {
        if (this.itemValue().graphic() instanceof SVGGlyph glyph) {
            glyph.stopWaiting();
        }
    }

    /**
     * 是否等待中
     *
     * @return 结果
     */
    public boolean isWaiting() {
        if (this.itemValue() != null && this.itemValue().graphic() instanceof SVGGlyph glyph) {
            return glyph.isWaiting();
        }
        return false;
    }

    /**
     * 自由处理
     * 如果是展开状态，则收缩节点
     * 如果是收缩状态，则展开节点
     */
    public void free() {
        if (this.isExpanded()) {
            this.collapse();
        } else {
            this.extend();
        }
    }

    /**
     * 重新展开
     */
    public void reExpanded() {
        if (this.isExpanded()) {
            FXUtil.runLater(() -> {
                this.setExpanded(false);
                this.setExpanded(true);
            });
        }
    }

    /**
     * 展开节点
     */
    public void extend() {
        FXUtil.runWait(() -> this.setExpanded(true));
    }

    /**
     * 收缩节点
     */
    public void collapse() {
        FXUtil.runWait(() -> this.setExpanded(false));
    }

    /**
     * 删除节点
     */
    public void delete() {
    }

    /**
     * 移除节点
     */
    public void remove() {
        if (this.getParent() != null) {
            this.getParent().getChildren().remove(this);
        } else {
            log.warn("remove fail, this.getParent() is null.");
        }
    }

    /**
     * 节点更名
     */
    public void rename() {
    }

    /**
     * 添加子节点
     *
     * @param item 节点
     */
    public void addChild(@NonNull TreeItem<?> item) {
        this.getChildren().add(item);
        this.sort(this.treeView().sortOrder());
    }

    /**
     * 移除子节点
     *
     * @param item 节点
     */
    public void removeChild(@NonNull TreeItem<?> item) {
        // 移除节点
        this.getChildren().remove(item);
    }

    /**
     * 移除多个子节点
     *
     * @param items 节点列表
     */
    public void removeChildes(@NonNull List<TreeItem<?>> items) {
        // 移除节点
        this.getChildren().removeAll(items);
    }

    /**
     * 子节点是否为空
     *
     * @return 结果
     */
    public boolean isChildEmpty() {
        return CollUtil.isEmpty(this.getChildren());
    }

    /**
     * 排序
     *
     * @param sortOrder 排序方式
     */
    public void sort(Boolean sortOrder) {
        if (sortOrder != null && !this.isChildEmpty()) {
            // 执行排序
            ObservableList<BaseTreeItem> subs = this.getChildren();
            if (sortOrder) {
                subs.sort((a, b) -> CharSequence.compare(a.itemValue().name(), b.itemValue().name()));
            } else {
                subs.sort((a, b) -> CharSequence.compare(b.itemValue().name(), a.itemValue().name()));
            }
        }
    }

    /**
     * 刷新图标
     */
    public void flushGraphic() {
        this.itemValue().flushGraphic();
        this.itemValue().flushGraphicColor();
    }

    /**
     * 获取右键菜单按钮列表
     *
     * @return 右键菜单按钮列表
     */
    public abstract List<MenuItem> getMenuItems();

    /**
     * 过滤
     */
    public void filter(@NonNull ZKTreeItemFilter filter) {

    }

    // /**
    //  * 获取节点组件
    //  *
    //  * @return Node
    //  */
    // public Node itemNode() {
    //     BaseTreeItemValue itemValue = this.itemValue();
    //     return itemValue == null ? null : itemValue.root();
    // }

    /**
     * 获取节点值
     *
     * @return ZKTreeItemValue
     */
    public BaseTreeItemValue itemValue() {
        return (BaseTreeItemValue) super.getValue();
    }

    /**
     * 设置节点值
     *
     * @param itemValue 节点值
     */
    public void itemValue(BaseTreeItemValue itemValue) {
        super.setValue(itemValue);
    }

    // /**
    //  * 设置节点值
    //  *
    //  * @param itemValue 节点值
    //  */
    // public void itemValue(String itemValue) {
    //     super.setValue(new BaseTreeItemValue(itemValue));
    // }

    // /**
    //  * 设置特效
    //  *
    //  * @param effect 特效
    //  */
    // public void setEffect(Effect effect) {
    //     if (this.itemNode() != null) {
    //         this.itemNode().setEffect(effect);
    //     }
    // }
    //
    // /**
    //  * 获取特效
    //  *
    //  * @return Effect
    //  */
    // public Effect getEffect() {
    //     if (this.itemNode() != null) {
    //         return this.itemNode().getEffect();
    //     }
    //     return null;
    // }

    @Override
    public Effect getDragEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3);
        shadow.setOffsetX(3);
        shadow.setColor(Color.RED);
        return shadow;
    }

    @Override
    public Effect getDropEffect() {
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3);
        shadow.setOffsetX(3);
        shadow.setColor(Color.DARKRED);
        return shadow;
    }
}
