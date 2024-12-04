package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.gui.treeView.RichTreeItem;
import cn.oyzh.fx.gui.treeView.RichTreeItemValue;
import cn.oyzh.fx.gui.treeView.RichTreeView;
import cn.oyzh.fx.plus.controls.svg.SVGGlyph;
import cn.oyzh.i18n.I18nHelper;
import javafx.scene.control.TreeItem;

/**
 * @author oyzh
 * @since 2023/1/30
 */
public class ZKDataTreeItem extends RichTreeItem<ZKDataTreeItem.ZKDataTreeItemValue> {

    public ZKDataTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKDataTreeItemValue());
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> treeItem = super.getParent();
        return (ZKConnectTreeItem) treeItem;
    }

    public ZKConnect connect() {
        return this.parent().value();
    }

    public ZKConnect zkConnect() {
        return this.parent().value();
    }

    @Override
    public void onPrimaryDoubleClick() {
        super.startWaiting(() -> ZKEventUtil.connectionOpened(this.parent()));
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKDataTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new SVGGlyph("/font/file-text.svg", 10);
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.data();
        }
    }
}
