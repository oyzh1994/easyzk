package cn.oyzh.easyzk.trees.connect;

import cn.oyzh.easyzk.domain.ZKConnect;
import cn.oyzh.easyzk.event.ZKEventUtil;
import cn.oyzh.fx.gui.svg.glyph.TerminalSVGGlyph;
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
public class ZKTerminalTreeItem extends RichTreeItem<ZKTerminalTreeItem.ZKTerminalTreeItemValue> {

    public ZKTerminalTreeItem(RichTreeView treeView) {
        super(treeView);
        this.setValue(new ZKTerminalTreeItemValue());
    }

    @Override
    public ZKConnectTreeItem parent() {
        TreeItem<?> parent = this.getParent();
        return (ZKConnectTreeItem) parent;
    }

    public ZKConnect zkConnect() {
        return this.parent().value();
    }

    @Override
    public void onPrimaryDoubleClick() {
        ZKEventUtil.terminalOpen(this.parent().value());
    }

    /**
     * zk树节点值
     *
     * @author oyzh
     * @since 2023/4/7
     */
    public static class ZKTerminalTreeItemValue extends RichTreeItemValue {

        @Override
        public SVGGlyph graphic() {
            if (this.graphic == null) {
                this.graphic = new TerminalSVGGlyph("10");
            }
            return super.graphic();
        }

        @Override
        public String name() {
            return I18nHelper.terminal();
        }
    }
}
