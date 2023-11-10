package cn.oyzh.easyzk.fx;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.fx.common.thread.Task;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.fx.plus.controls.tree.FlexTreeView;
import cn.oyzh.fx.plus.drag.DragNodeItem;
import cn.oyzh.fx.plus.trees.RichTreeItem;
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
public abstract class ZKTreeItem extends RichTreeItem   {

    /**
     * zk树
     */
    @Getter
    @Accessors(chain = true, fluent = true)
    private ZKTreeView treeView;

    @Override
    public ZKTreeItemValue itemValue() {
        return (ZKTreeItemValue) super.getValue();
    }

    @Override
    public void treeView(FlexTreeView treeView) {
        this.treeView = (ZKTreeView) treeView;
    }
}
